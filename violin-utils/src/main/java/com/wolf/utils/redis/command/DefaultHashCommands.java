
package com.wolf.utils.redis.command;


import com.wolf.utils.redis.RedisThreadPool;
import com.wolf.utils.redis.TedisConnectionException;
import com.wolf.utils.redis.TedisException;
import com.wolf.utils.redis.threadpool.IAsynchronousHandler;

import java.util.*;
import java.util.concurrent.Future;

/**
 * 
 *  
 * <br/> Created on 2014-7-3 下午1:55:19

 * @since 3.3
 */
public class DefaultHashCommands extends DefaultCommands implements HashCommands {

    public DefaultHashCommands(String groupName) {
    	super(groupName);
    }

    @Override
    public <H, HK, HV> boolean delete(final int namespace, final H key, final Object... hashKey) {
    	return delete(namespace, key, false, hashKey);
    }
    
    @Override
    public <H, HK, HV> boolean delete(final int namespace, final H key, final boolean useNewKeySerialize, final Object... hashKey) {
        for(int i = 0 ;i < REPEAT_NUM;i++){
        	try {
        		return this.deleteWithoutTry(namespace, key, useNewKeySerialize, hashKey);
        	} catch (TedisConnectionException e) {
				if(ERROR_MESSAGE.equals(e.getMessage()) && i < REPEAT_NUM - 1) {
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e1) {
					}
					continue;
				}else {
					throw new TedisException(e);
				}
			}
        }
        throw new TedisException("执行hash的delete失败！");
    }

    private <H, HK, HV> boolean deleteWithoutTry(final int namespace, final H key, final boolean useNewKeySerialize, final Object... hashKey) {
        return isBoolean(doInTedis(namespace,  new TedisBlock(namespace,String.valueOf(key) ,groupName) {
            @Override
            public Object execute() {
                return commands.hDel(rawKey(namespace, key, useNewKeySerialize), rawHashKeys(hashKey));
            }
        }));
    }

    @Override
    public <H, HK, HV> boolean ndelete(int namespace, H key, Object... hashKey) {
        return ndelete(false,namespace, key, hashKey);
    }

    @Override
    public <H, HK, HV> boolean ndelete( boolean useNewKeySerialize,int namespace, H key, Object... hashKey) {
        for(int i = 0 ;i < REPEAT_NUM;i++){
            try {
                return this.deleteWithoutTry(namespace, key, useNewKeySerialize, hashKey);
            } catch (TedisConnectionException e) {
                if(ERROR_MESSAGE.equals(e.getMessage()) && i < REPEAT_NUM - 1) {
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }else {
                    throw new TedisException(e);
                }
            }
        }
        throw new TedisException("执行hash的delete失败！");
    }

    @Override
    public <H, HK, HV> Map<HK, HV> entries(final int namespace, final H key) {
        return entries(namespace, key, false);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <H, HK, HV> Map<HK, HV> entries(final int namespace, final H key, final boolean useNewKeySerialize) {
        return deserializeHashMap((Map<byte[], byte[]>)doInTedis(namespace,  new TedisBlock(namespace,String.valueOf(key) ,groupName , READ) {
            @Override
            public Object execute() {
                return commands.hGetAll(rawKey(namespace, key, useNewKeySerialize));
            }
        }));
    }

    @Override
    public <H, HK, HV> HV get(final int namespace, final H key, final Object hashKey) {
        return get(namespace, key, hashKey, false);
    }
    
    @Override
    public <H, HK, HV> HV get(final int namespace, final H key, final Object hashKey, final boolean useNewKeySerialize) {
        return deserializeHashValue((byte[])doInTedis(namespace, new TedisBlock(namespace,String.valueOf(key) ,groupName,READ) {
            @Override
            public Object execute() {
                return commands.hGet(rawKey(namespace, key, useNewKeySerialize), rawHashKey(hashKey));
            }
        }));
    }

    @Override
    public <H, HK, HV> Boolean hasKey(final int namespace, final H key, final Object hashKey) {
        return hasKey(namespace, key, hashKey, false);
    }
    
    @Override
    public <H, HK, HV> Boolean hasKey(final int namespace, final H key, final Object hashKey, final boolean useNewKeySerialize) {
        return (Boolean)doInTedis(namespace,  new TedisBlock(namespace,String.valueOf(key) ,groupName,READ) {
            @Override
            public Object execute() {
                return commands.hExists(rawKey(namespace, key, useNewKeySerialize), rawHashKey(hashKey));
            }
        });
    }

    @Override
    public <H, HK, HV> Long increment(final int namespace, final H key, final HK hashKey, final long delta) {
        return increment(namespace, key, hashKey, delta, false);
    }
    
    @Override
    public <H, HK, HV> Long increment(final int namespace, final H key, final HK hashKey, final long delta, final boolean useNewKeySerialize) {
        for(int i = 0 ;i < REPEAT_NUM;i++){
        	try {
        		return this.incrementWithoutTry(namespace, key, hashKey, delta, useNewKeySerialize);
        	} catch (TedisConnectionException e) {
				if(ERROR_MESSAGE.equals(e.getMessage()) && i < REPEAT_NUM - 1) {
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e1) {
					}
					continue;
				}else {
					throw new TedisException(e);
				}
			}
        }
        throw new TedisException("执行hash的increment失败！");
    }
    
    private <H, HK, HV> Long incrementWithoutTry(final int namespace, final H key, final HK hashKey, final long delta, final boolean useNewKeySerialize) {
        return (Long)doInTedis(namespace,  new TedisBlock(namespace,String.valueOf(key) ,groupName) {
            @Override
            public Object execute() {
                return commands.hIncrBy(rawKey(namespace, key, useNewKeySerialize), rawHashKey(hashKey), delta);
            }
        });
    }

    @Override
    @Deprecated
    public <H, HK, HV> Set<HK> keys(final int namespace, final H key) {
        return keys(namespace, key, false);
    }
    
    @Override
    @Deprecated
    public <H, HK, HV> Set<HK> keys(final int namespace, final H key, final boolean useNewKeySerialize) {
        return hKeys(namespace, key, useNewKeySerialize);
    }
    
    @Override
	public <H, HK, HV> Set<HK> hKeys(final int namespace,final H key) {
		return this.hKeys(namespace, key, false);
	}

    @SuppressWarnings("unchecked")
	@Override
	public <H, HK, HV> Set<HK> hKeys(final int namespace, final H key, final boolean useNewKeySerialize) {
		return deserializeHashKeys((Set<byte[]>)doInTedis(namespace,  new TedisBlock(namespace,String.valueOf(key) ,groupName,READ) {
            @Override
            public Object execute() {
                return commands.hKeys(rawKey(namespace, key, useNewKeySerialize));
            }
        }));
	}

    @Override
    public <H, HK, HV> Collection<HV> multiGet(final int namespace, H key, Collection<HK> hashKeys) {
        return multiGet(namespace, key, hashKeys, false);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <H, HK, HV> Collection<HV> multiGet(final int namespace, H key, Collection<HK> hashKeys, final boolean useNewKeySerialize) {
        if (hashKeys == null || hashKeys.isEmpty()) {
            return Collections.emptyList();
        }

        final byte[] rawKey = rawKey(namespace, key, useNewKeySerialize);

        final byte[][] rawHashKeys = new byte[hashKeys.size()][];

        int counter = 0;
        for (HK hashKey : hashKeys) {
            rawHashKeys[counter++] = rawHashKey(hashKey);
        }
        return deserializeHashValues((List<byte[]>)doInTedis(namespace,  new TedisBlock(namespace,String.valueOf(key) ,groupName,READ) {
            @Override
            public Object execute() {
                return commands.hMGet(rawKey, rawHashKeys);
            }
        }));
    }

    @Override
    public <H, HK, HV> void put(final int namespace, final H key, final HK hashKey, final HV value) {
         put(namespace, key, hashKey, value, false);
    }
    
    @Override
    public <H, HK, HV> void put(final int namespace, final H key, final HK hashKey, final HV value, final boolean useNewKeySerialize) {
        for(int i = 0 ;i < REPEAT_NUM;i++){
        	try {
        		this.putWithoutTry(namespace, key, hashKey, value, useNewKeySerialize);
        		return;
        	} catch (TedisConnectionException e) {
				if(ERROR_MESSAGE.equals(e.getMessage()) && i < REPEAT_NUM - 1) {
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e1) {
					}
					continue;
				}else {
					throw new TedisException(e);
				}
			}
        }
    }
    
    private <H, HK, HV> void putWithoutTry(final int namespace, final H key, final HK hashKey, final HV value, final boolean useNewKeySerialize) {
        doInTedis(namespace,  new TedisBlock(namespace,String.valueOf(key) ,groupName) {
            @Override
            public Object execute() {
                return commands.hSet(rawKey(namespace, key, useNewKeySerialize), rawHashKey(hashKey), rawHashValue(value));
            }
        });
    }

    @Override
    public <H, HK, HV> void putAll(final int namespace, H key, Map<? extends HK, ? extends HV> m) {
        putAll(namespace, key, m, false);
    }
    
    @Override
    public <H, HK, HV> void putAll(final int namespace, H key, Map<? extends HK, ? extends HV> m, final boolean useNewKeySerialize) {
        if (m.isEmpty()) {
            return;
        }

        final byte[] rawKey = rawKey(namespace, key, useNewKeySerialize);

        final Map<byte[], byte[]> hashes = new LinkedHashMap<byte[], byte[]>(m.size());

        for (Map.Entry<? extends HK, ? extends HV> entry : m.entrySet()) {
            hashes.put(rawHashKey(entry.getKey()), rawHashValue(entry.getValue()));
        }
        
        for(int i = 0 ;i < REPEAT_NUM;i++){
        	try {
        		
        		doInTedis(namespace,  new TedisBlock(namespace,String.valueOf(key) ,groupName) {
                    @Override
                    public Object execute() {
                        commands.hMSet(rawKey, hashes);
                        return null;
                    }
                });
        		
        		return;
        	} catch (TedisConnectionException e) {
				if(ERROR_MESSAGE.equals(e.getMessage()) && i < REPEAT_NUM - 1) {
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e1) {
					}
					continue;
				}else {
					throw new TedisException(e);
				}
			}
        }
    }
    
    @Override
    public <H, HK, HV> Boolean putIfAbsent(final int namespace, final H key, final HK hashKey, final HV value) {
        return putIfAbsent(namespace, key, hashKey, value, false);
    }
    
    @Override
    public <H, HK, HV> Boolean putIfAbsent(final int namespace, final H key, final HK hashKey, final HV value, final boolean useNewKeySerialize) {
    	for(int i = 0 ;i < REPEAT_NUM;i++){
        	try {
        		return this.putIfAbsentWithoutTry(namespace, key, hashKey, value, useNewKeySerialize);
        	} catch (TedisConnectionException e) {
				if(ERROR_MESSAGE.equals(e.getMessage()) && i < REPEAT_NUM - 1) {
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e1) {
					}
					continue;
				}else {
					throw new TedisException(e);
				}
			}
        }
    	throw new TedisException("执行hash的putIfAbsent失败！");
    }
    
    private <H, HK, HV> Boolean putIfAbsentWithoutTry(final int namespace, final H key, final HK hashKey, final HV value, final boolean useNewKeySerialize) {
        return (Boolean)doInTedis(namespace,  new TedisBlock(namespace,String.valueOf(key) ,groupName) {
            @Override
            public Object execute() {
                return commands.hSetNX(rawKey(namespace, key, useNewKeySerialize), rawHashKey(hashKey), rawHashValue(value));
            }
        });
    }

    @Override
    public <H, HK, HV> Long size(final int namespace, final H key) {
        return size(namespace, key, false);
    }
    
    @Override
    public <H, HK, HV> Long size(final int namespace, final H key, final boolean useNewKeySerialize) {
        return (Long)doInTedis(namespace,  new TedisBlock(namespace,String.valueOf(key) ,groupName,READ) {
            @Override
            public Object execute() {
                return commands.hLen(rawKey(namespace, key, useNewKeySerialize));
            }
        });
    }

    @Override
    public <H, HK, HV> Collection<HV> values(final int namespace, final H key) {
        return values(namespace, key, false);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <H, HK, HV> Collection<HV> values(final int namespace, final H key, final boolean useNewKeySerialize) {
        return deserializeHashValues((List<byte[]>)doInTedis(namespace,  new TedisBlock(namespace,String.valueOf(key) ,groupName,READ) {
            @Override
            public Object execute() {
                return commands.hVals(rawKey(namespace, key, useNewKeySerialize));
            }
        }));
    }
   
	@Override
	public <H, HK, HV> List<Map<HK, HV>> multiEntries(int namespace,
			Collection<H> keys) {
		return multiEntries(namespace, keys, false, false);
	}

	@Override
	public <H, HK, HV> List<Map<HK, HV>> multiEntries(int namespace,
			Collection<H> keys, boolean useNewKeySerialize, boolean isThread) {
		Map<String,List<H>> map = groupForKeys(namespace, groupName, keys);
		if(map == null || map.size() == 0){
			return null;
		}
        if(isThread && map.size() > 1) {
        	try {
				return getResultByThread(namespace, keys, useNewKeySerialize, map);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
        }else {
        	return getResultNotThread(namespace, keys, useNewKeySerialize, map);
        }
        
	}
	
	@SuppressWarnings("unchecked")
	private <H, HK, HV> List<Map<HK, HV>> getResultNotThread(final int namespace,
			final Collection<H> keys, final boolean useNewKeySerialize, Map<String, List<H>> map) {
        Iterator<Map.Entry<String, List<H>>> ito = map.entrySet().iterator();
        List<Map<HK, HV>> resultList = new ArrayList<Map<HK,HV>>();
        while(ito.hasNext()) {
        	final List<H> keyC = ito.next().getValue();
        	List<Map<HK, HV>> temList = deserializeHashMaps((List<Map<byte[], byte[]>>)doInTedis(namespace,  new TedisBlock(namespace,String.valueOf(keyC.get(0)) ,groupName,READ) {
        		@Override
        		public Object execute() {
        			return commands.hGetAllKeys(rawKeys(namespace, keyC, useNewKeySerialize));
        		}
        	}));
        	
        	if(temList.size() > 0 ){
        		resultList.addAll(temList);
        	}
        }
        return resultList;
	}
	
	@SuppressWarnings("unchecked")
	private <H, HK, HV> List<Map<HK, HV>> getResultByThread(final int namespace,
			final Collection<H> keys, final boolean useNewKeySerialize, Map<String, List<H>> map) throws Exception {
	    final Iterator<Map.Entry<String, List<H>>> ito = map.entrySet().iterator();
	    List<Map<HK, HV>> resultList = new ArrayList<Map<HK,HV>>();
	     
		Integer threadNum = map.size();
		Queue<Future<Object>> fQueue = new LinkedList<Future<Object>>();
		for(int i = 0 ; i < threadNum; i++){
			Future<Object> future =  RedisThreadPool.submit(new IAsynchronousHandler() {
				
				@Override
				public Object call() throws Exception {
					final List<H> keyC = ito.next().getValue();
					return deserializeHashMaps((List<Map<byte[], byte[]>>)doInTedis(namespace,  new TedisBlock(namespace,String.valueOf(keyC.get(0)) ,groupName,READ) {
						@Override
						public Object execute() {
								
								return commands.hGetAllKeys(rawKeys(namespace, keyC, useNewKeySerialize));
						}
					}));
				}

				@Override
				public void executeAfter(Throwable t) {}

				@Override
				public void executeBefore(Thread t) {}
			});
			fQueue.offer(future);
		}
		
		Future<Object> future = null;
		Long t = System.currentTimeMillis();
		while((future = fQueue.poll()) != null) {
			
			if(System.currentTimeMillis() - t > 2000) {
				throw new RuntimeException("多线程查询超时， 超时时间2s！");
			}
			
			if(future.isDone()) {
				Object obj = future.get();
				if(obj == null) {
					throw new RuntimeException("多线程查询失败！");
				}
				List<Map<HK, HV>> temList = (List<Map<HK, HV>>)obj;
				if(temList.size() > 0) {
					resultList.addAll(temList);
				}
			}else {
				fQueue.offer(future);
			}
		}
		
		return resultList;
	}

}
