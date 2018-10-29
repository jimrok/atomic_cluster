package ewhine.config;


import java.util.Map;

public class ConfigSet {
//	private static Logger log = LoggerFactory.getLogger(ConfigSet.class);

    private Map<String, Object> configMap = null;

    // public String get(String name) {
    // return props.getProperty(name);
    // }
    //
    // public Properties getProps() {
    // return props;
    // }
    public ConfigSet(Map<String, Object> _configMap) {
        this.configMap = _configMap;
    }

    public int getIntValue(String _key, int defaultValue) {
        Object value = configMap.get(_key);
        if (value == null) {
            return defaultValue;
        }

        return (int) value;
    }

    public int getIntValue(String key) {
        return getIntValue(key, 0);
    }

    public boolean getBooleanValue(String _key, boolean defaultValue) {
        Object value = configMap.get(_key);
        if (value == null) {
            return defaultValue;
        }

        return (boolean) value;
    }

    public boolean getBooleanValue(String _key) {
        return getBooleanValue(_key, false);
    }

    public String getValue(String key, String defaultValue) {
        Object value = configMap.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }

    public String getValue(String key) {
        return getValue(key, null);
    }
    
    public void setValue(String key,Object value) {
    	configMap.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getConfig(String key) {
    	
    	if (configMap == null) {
    		return null;
    	}

        Object value = configMap.get(key);

        if (value != null && value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }

}
