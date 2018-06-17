package org.grohe.ondus.api;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.grohe.ondus.api.model.Authentication;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.grohe.ondus.api.TestResponse.A_TOKEN;
import static org.grohe.ondus.api.TestResponse.VALID_LOGIN_RESPONSE;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApiClientTest {
    private static final BasicHttpResponse EXAMPLE_RESPONSE_500 = new BasicHttpResponse(new BasicStatusLine(
            new ProtocolVersion("HTTP", 1, 1), 500, ""));
    private static final String TEST_BASE_URL = "";

    @Test
    public void get_callsHttpClientWithGetAndToken() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.execute(any())).thenReturn(getOkResponse());
        ApiClient client = new ApiClient(TEST_BASE_URL, httpClient);

        client.get("/v2/info", A_TOKEN, Object.class);

        ArgumentCaptor<HttpGet> captor = ArgumentCaptor.forClass(HttpGet.class);
        verify(httpClient).execute(captor.capture());
        HttpGet actualRequest = captor.getValue();
        assertEquals(A_TOKEN, actualRequest.getHeaders("Authorization")[0].getValue());
    }

    @Test
    public void get_non200Response_returnsEmptyOptional() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.execute(any())).thenReturn(EXAMPLE_RESPONSE_500);
        ApiClient client = new ApiClient(TEST_BASE_URL, httpClient);

        Optional<Object> response = client.get("/v2/auth", "", Object.class);

        assertFalse(response.isPresent());
    }

    @Test
    public void get_200Response_returnsClassObject() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.execute(any())).thenReturn(getOkResponse());
        ApiClient client = new ApiClient(TEST_BASE_URL, httpClient);

        Optional<Authentication> response = client.get("/v2/auth", "", Authentication.class);

        assertTrue(response.isPresent());
        assertEquals(TestResponse.A_TOKEN, response.orElseThrow(NoSuchElementException::new).getToken());
    }

    @Test
    public void post_callsHttpClientWithPost() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.execute(any())).thenReturn(getOkResponse());
        ApiClient client = new ApiClient(TEST_BASE_URL, httpClient);

        client.post("/v2/info", Object.class);

        verify(httpClient).execute(any(HttpPost.class));
    }

    @Test
    public void post_non200Response_returnsEmptyOptional() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.execute(any())).thenReturn(EXAMPLE_RESPONSE_500);
        ApiClient client = new ApiClient(TEST_BASE_URL, httpClient);

        Optional<Object> response = client.post("/v2/auth", Object.class);

        assertFalse(response.isPresent());
    }

    @Test
    public void post_200Response_returnsClassObject() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.execute(any())).thenReturn(getOkResponse());
        ApiClient client = new ApiClient(TEST_BASE_URL, httpClient);

        Optional<Authentication> response = client.post("/v2/auth", Authentication.class);

        assertTrue(response.isPresent());
        assertEquals(TestResponse.A_TOKEN, response.orElseThrow(NoSuchElementException::new).getToken());
    }

    private HttpResponse getOkResponse() {
        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(
                new ProtocolVersion("HTTP", 1, 1), 200, "OK"));

        BasicHttpEntity contentEntity = new BasicHttpEntity();
        contentEntity.setContent(new ByteArrayInputStream(VALID_LOGIN_RESPONSE.getBytes()));
        response.setEntity(contentEntity);
        return response;
    }
}