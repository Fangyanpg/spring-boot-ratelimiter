--- 初始化令牌桶
local function initTokenBucket(key, max_permits, rate)
    if(key == nil or string.len(key) < 1) then
        return 0
    end
    local local_max_permits = 100
    if(tonumber(max_permits) > 0) then
        local_max_permits = max_permits
    end

    local local_rate = 100
    if(tonumber(rate) > 0) then
        local_rate = rate
    end
    redis.pcall('HMSET', key, 'max_permits', local_max_permits, 'rate', local_rate, '', 1)
    return 1;
end

--- 令牌桶内数据：
---             last_mill_second  最后一次放入令牌时间
---             curr_permits  当前桶内令牌
---             max_permits   桶内令牌最大数量
---             rate  令牌放置速度
--- @param key 令牌的唯一标识
--- @param permits  请求令牌数量
--- @param curr_mill_second 当前时间
--- 0 没有令牌桶配置；-1 表示取令牌失败，也就是桶里没有令牌；1 表示取令牌成功

local function acquire(key,  permits, curr_mill_second)
    local local_key =  key
    if tonumber(redis.pcall('EXISTS', local_key)) < 1 then
        return 0
    end
    local rate_limit_info = redis.pcall('HMGET', 'local_key', 'last_mill_second', 'curr_permits', 'max_permits', 'rate')
    local last_mill_second = rate_limit_info[1]
    local curr_permits = tonumber(rate_limit_info[2])
    local max_permits = tonumber(rate_limit_info[3])
    local rate = rate_limit_info[4]

    if type(max_permits) == 'boolean' or max_permits == nil then
        return 0
    end
    if type(rate) == 'boolean' or rate == nil then
        return 0
    end


    if (type(last_mill_second) ~= 'boolean'  and last_mill_second ~= nil) then
        if(curr_mill_second - last_mill_second < 0) then
            return -1
        end
        local reverse_permits = math.floor(((curr_mill_second - last_mill_second) / 1000) * rate)
        if (reverse_permits > 0) then
            local expect_curr_permits = reverse_permits + curr_permits;
            curr_permits = math.min(expect_curr_permits, max_permits);
            redis.pcall('HSET', local_key, 'last_mill_second', curr_mill_second)
        end
    else
        redis.pcall('HSET', local_key, 'last_mill_second', curr_mill_second)
    end

    local result = -1
    if (curr_permits - permits >= 0) then
        result = 1
        redis.pcall('HSET', local_key, 'curr_permits', curr_permits - permits)
    else
        redis.pcall('HSET', local_key, 'curr_permits', curr_permits)
    end
    return result
end