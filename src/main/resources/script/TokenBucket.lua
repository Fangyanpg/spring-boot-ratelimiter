
--- @param key 令牌的唯一标识
--- @param permits  请求令牌数量
--- @param currMillSecond 当前时间
--- @param maxPermits 最大限制数 limit
--- @param rate 增量 limit/expire
local function acquire(key,  permits, currMillSecond, maxPermits, rate)
    if tonumber(redis.pcall('EXISTS', key)) < 1 then
        --- 初始化令牌桶
        redis.pcall('HMSET', key, 'maxPermits', maxPermits, 'rate', rate, 'currPermits', 1)
    end
    local rateLimitLnfo = redis.pcall('HMGET', key, 'lastMillSecond', 'currPermits', 'maxPermits', 'rate')
    local lastMillSecond = rateLimitLnfo[1]
    local currPermits = tonumber(rateLimitLnfo[2])

    if (type(lastMillSecond) ~= 'boolean'  and lastMillSecond ~= nil) then
        if(currMillSecond - lastMillSecond < 0) then
            return 'false'
        end
        local reversePermits = math.floor(((currMillSecond - lastMillSecond) / 1000) * rate)
        if (reversePermits > 0) then
            local expectCurrPermits = reversePermits + currPermits;
            currPermits = math.min(expectCurrPermits, maxPermits);
            redis.pcall('HSET', key, 'lastMillSecond', currMillSecond)
        end
    else
        redis.pcall('HSET', key, 'lastMillSecond', currMillSecond)
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

local key = KEYS[1];
local permits = ARGV[1];
local currMillSecond = ARGV[2];
local maxPermits = ARGV[3];
local rate = ARGV[4];
return acquire(key, permits, currMillSecond, maxPermits, rate)