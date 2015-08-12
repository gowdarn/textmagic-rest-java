package com.textmagic.sdk.resource;

import com.textmagic.sdk.RestClient;
import com.textmagic.sdk.RestException;
import com.textmagic.sdk.RestResponse;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class InstanceResource<C extends RestClient> extends Resource<C> {

    /**
     * Resource properties
     */
	protected Map<String, Object> properties;
	
	/**
	 * Instantiates resource
	 *
	 * @param client HTTP client
	 */
	public InstanceResource(final C client) {
		super(client);
		clearProperties();
		clearParameters();
	}

	/**
	 * Instantiates resource
	 *
	 * @param client HTTP client
	 * @param properties Resource properties
	 */
	public InstanceResource(final C client, final Map<String, Object> properties) {
		super(client);
		clearParameters();
		if (properties != null && properties.size() > 0) {
			this.properties = new HashMap<String, Object>(properties);
		} else {
			this.properties = new HashMap<String, Object>();
		}
	}

	/**
	 * Clear properties
	 */
	protected void clearProperties() {
		properties = new HashMap<String, Object>();
	}

	/**
	 * Clear parameters
	 */
	protected void clearParameters() {
		parameters = new HashMap<String, String>();
	}	
	
	/**
	 * Retrieve resource property
	 *
	 * @param name Property name
	 * @return Property value
	 */
	protected Object getProperty(String name) {
		return properties.get(name);
	}

	/**
	 * Set resource property
	 *
	 * @param Property name
	 * @param Property value
	 */
	protected void setProperty(String name, Object value) {
		properties.put(name, value);
	}    
    
	/**
	 * Retrieve resource date property
	 * @param name Property name
	 * @return Property value
	 */
	protected Date getDate(String name) {
        String property = (String) getProperty(name);
        
        if (property == null) {
			return null;
		}
		try {
            return DateUtils.parseDateStrictly(property, new String[] {"yyyy-MM-dd'T'HH:mm:ssZ"});
        } catch (ParseException e) {
			return null;
		}
	}
    
	/**
	 * Set resource date property
	 *
	 * @param Property name
	 * @param Property value
	 */
    protected void setDate(String name, Date value) {
        properties.put(name, DateFormatUtils.format(value, "yyyy-MM-dd'T'HH:mm:ssZ"));
    }
    
	/**
	 * Get resource item
	 *
	 * @param id Resource item id
	 * @throws RestException
	 */
	public boolean get(Integer id) throws RestException {
		if (properties.size() == 0) {
            RestResponse response = getClient().request(getResourcePath() + '/' + id, "GET");
            this.properties = new HashMap<String, Object>(response.toMap());
            
            return !response.isError();
		} else {
			throw new UnsupportedOperationException("This operation is unsupported for existent objects");
		}
	}
	
	/**
	 * Create or update resource item
	 * @throws RestException
	 */
	public boolean createOrUpdate() throws RestException {
		String resourcePath = null;
		String method = null;
		
		if (getProperty("id") == null) {
			method = "POST";
			resourcePath = getResourcePath();
		} else {
			method = "PUT";
			resourcePath = getResourcePath() + '/' + getProperty("id");
		}
		
        RestResponse response = getClient().request(resourcePath, method, buildRequestParameters(properties));
        Map<String, Object> properties = response.toMap();
        Integer id = (Integer) properties.get("id");
        clearProperties();
        return get(id);
	}
	
	/**
	 * Delete resource item
	 * @throws RestException
	 */
	public boolean delete() throws RestException {
		if (getProperty("id") == null) {
			throw new UnsupportedOperationException("This operation is unsupported for non existent objects");
		} else {
			RestResponse response = getClient().request(getResourcePath() + '/' + getProperty("id"), "DELETE");
            clearProperties();
            
            return !response.isError();
		}
	}
}