local count = tonumber(redis.call('incr', KEYS[1]))
local ttl = redis.call('ttl', KEYS[1])
if (count) == 1
then
    redis.call('expire', KEYS[1], tonumber(ARGV[2]))
elseif ttl == -1
then
    redis.call('expire', KEYS[1], tonumber(ARGV[2]))
end
if (count > tonumber(ARGV[1]))
then
    return 'false'
end
return 'true'