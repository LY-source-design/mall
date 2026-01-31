local stockKey = KEYS[1]
local qualifiedSetKey = KEYS[2]
local userId = ARGV[1]

if(redis.call("SISMEMBER", qualifiedSetKey, userId) == 1) then
    redis.call("SREM", qualifiedSetKey, userId)
    redis.call("INCR", stockKey)
    return 1
else
    return 0
end