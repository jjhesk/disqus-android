package me.philio.disqus.api.resources;

import android.test.suitebuilder.annotation.LargeTest;

import me.philio.disqus.api.exception.ApiException;
import me.philio.disqus.api.model.Response;
import me.philio.disqus.api.model.applications.Usage;

public class ApplicationsTest extends ResourceTestCase {

    /**
     * Applications resource
     */
    private Applications mApplications;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mApplications = mApiClient.createApplications();
    }

    /**
     * Test that listUsage returns some valid data
     *
     * @throws ApiException
     */
    @LargeTest
    public void testListUsage() throws ApiException {
        Response<Usage> usage = mApplications.listUsage();
        assertNotNull(usage);
        assertNotNull(usage.data);
        assertTrue(usage.data.size() > 0);
    }

    /**
     * Test that listUsage returns the requested number of days data
     *
     * API always returns days 1 extra day than requested
     *
     * @throws ApiException
     */
    @LargeTest
    public void testListUsageDays() throws ApiException {
        Response<Usage> usage = mApplications.listUsage(6);
        assertNotNull(usage);
        assertNotNull(usage.data);
        assertEquals(7, usage.data.size());
    }

    /**
     * Test that listUsage returns the requested number of days data for the specified application
     *
     * API always returns days 1 extra day than requested
     *
     * @throws ApiException
     */
    @LargeTest
    public void testListUsageAppDays() throws ApiException {
        Response<Usage> usage = mApplications.listUsage(3318003, 6);
        assertNotNull(usage);
        assertNotNull(usage.data);
        assertEquals(7, usage.data.size());
    }

}