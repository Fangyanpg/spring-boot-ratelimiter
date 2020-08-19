

--- 令牌桶内数据：
---             lastMillSecond  最后一次放入令牌时间
---             currPermits  当前桶内令牌
---             maxPermits   桶内令牌最大数量
---             rate  令牌放置速度
--- @param key 令牌的唯一标识
--- @param permits  请求令牌数量
--- @param curr_mill_second 当前时间

local function acquire(key,  permits, curr_mill_second)
    if tonumber(redis.pcall('EXISTS', key)) < 1 then
        --- 初始化令牌桶
        redis.pcall('HMSET', key, 'maxPermits', limit, 'rate', rate, 'currPermits', 1)
    end
    local rateLimitLnfo = redis.pcall('HMGET', key, 'lastMillSecond', 'currPermits', 'maxPermits', 'rate')
    local lastMillSecond = rateLimitLnfo[1]
    local currPermits = tonumber(rateLimitLnfo[2])
    local maxPermits = tonumber(rateLimitLnfo[3])
    local rate = tonumber(rateLimitLnfo[4])

    if (type(lastMillSecond) ~= 'boolean'  and lastMillSecond ~= nil) then
        if(curr_mill_second - lastMillSecond < 0) then
            return 'false'
        end
        local reversePermits = math.floor(((curr_mill_second - lastMillSecond) / 1000) * rate)
        if (reversePermits > 0) then
            local expectCurrPermits = reversePermits + currPermits;
            currPermits = math.min(expectCurrPermits, maxPermits);
            redis.pcall('HSET', key, 'lastMillSecond', curr_mill_second)
        end
    else
        redis.pcall('HSET', key, 'lastMillSecond', curr_mill_second)
    end

    local result = 'false'
    if (currPermits - permits >= 0) then
        result = 'true'
        redis.pcall('HSET', key, 'currPermits', currPermits - permits)
    else
        redis.pcall('HSET', key, 'currPermits', currPermits)
    end
    return result
end