local qualifiedSetKey = KEYS[1];
local purchasedSetKey = KEYS[2];
local userId = ARGV[1];

if (redis.call("SISMEMBER", qualifiedSetKey, userId) == 0) then
    return 1
end

redis.call("SREM", qualifiedSetKey, userId);
redis.call("SADD", purchasedSetKey, userId);
return 0
