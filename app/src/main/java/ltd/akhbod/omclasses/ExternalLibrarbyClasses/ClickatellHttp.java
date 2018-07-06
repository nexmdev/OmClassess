package ltd.akhbod.omclasses.ExternalLibrarbyClasses;



        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.ClientProtocolException;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.message.BasicNameValuePair;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.UnknownHostException;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

/**
 * This is an example of how to use the Clickatell HTTP API. NOTE: this is not
 * the only way, this is just an example. This class can also be used as a
 * library if you wish. I have tried to catch all the errors that I think I should.
 * There are some errors that I pass on the calling function. And should be checked there.
 *
 * @author Dominic Schaff <dominic.schaff@gmail.com>
 */
public class ClickatellHttp {

    /**
     * The URL to use for the base of the HTTP API.
     */
    private static final String CLICKATELL_HTTP_BASE_URL = "https://api.clickatell.com/http/";

    /**
     * The URL to use for the base of the HTTP/UTILS API.
     */
    private static final String CLICKATELL_UTILS_BASE_URL = "https://api.clickatell.com/utils/";

    /**
     * The three private variables to use for authentication.
     */
    private final String userName;
    private final String apiId;
    private final String password;

    /**
     * Create a HTTP object, and set the auth, but not test the auth.
     */
    public ClickatellHttp(String userName, String apiId, String password) {
        this.userName = userName;
        this.apiId = apiId;
        this.password = password;
    }

    /**
     * This tests whether your account details works.
     *
     * @return True if details were accepted, and false otherwise.
     * @throws java.net.UnknownHostException This is thrown if the HOST cannot be found. Maybe you are not on the internet?
     */
    public boolean testAuth() throws UnknownHostException {
        // Build Parameters:
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("user", this.userName));
        nameValuePairs.add(new BasicNameValuePair("api_id", this.apiId));
        nameValuePairs.add(new BasicNameValuePair("password", this.password));

        // Send Request:
        String s = this.executePost(CLICKATELL_HTTP_BASE_URL + "auth.php",
                nameValuePairs);
        // Check whether an auth failed happened:
        return !s.equalsIgnoreCase("ERR: 001, Authentication failed");
    }

    /**
     * This will attempt to get your current balance.
     *
     * @return Your balance.
     * @throws java.net.UnknownHostException This is thrown if the HOST cannot be found. Maybe you are not on the internet?
     * @throws Exception                     There are errors that get thrown, you should catch them.
     */
    public double getBalance() throws Exception {
        // Build Parameters:
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("user", this.userName));
        nameValuePairs.add(new BasicNameValuePair("api_id", this.apiId));
        nameValuePairs.add(new BasicNameValuePair("password", this.password));

        // Send Request:
        String s = this.executePost(CLICKATELL_HTTP_BASE_URL + "getbalance.php",
                nameValuePairs);
        // Check whether an auth failed happened:
        if (s.contains("Authentication failed")) {
            throw new Exception("Authentication Failed");
        }
        // We know the balance is the second part of the query:
        String[] a = s.split(" ");
        return Double.parseDouble(a[1]);
    }

    /**
     * This sends a single message.
     *
     * @param number  The number that you wish to send to. This should be in
     *                international format.
     * @param message The message you want to send,
     * @return A message object, which will contain either the message ID, or an error message.
     * @throws java.net.UnknownHostException This is thrown if the HOST cannot be found. Maybe you are not on the internet?
     * @throws Exception                     There are errors that get thrown, you should catch them.
     */
    public Message sendMessage(String number, String message) throws Exception {
        // Build Parameters:
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("user", this.userName));
        nameValuePairs.add(new BasicNameValuePair("api_id", this.apiId));
        nameValuePairs.add(new BasicNameValuePair("password", this.password));
        nameValuePairs.add(new BasicNameValuePair("to", number));
        nameValuePairs.add(new BasicNameValuePair("text", message));

        // Send Request:
        String s = this.executePost(CLICKATELL_HTTP_BASE_URL + "sendmsg.php", nameValuePairs);
        // Check whether an auth failed happened:
        if (s.contains("Authentication failed")) {
            throw new Exception("Authentication Failed");
        }
        String a[] = s.split(": ");
        Message m = new Message();
        m.number = number;
        m.content = message;
        // Check whether there is no credit left in the account:
        if (s.equalsIgnoreCase("err")) {
            m.error = a[1];
            return m;
        }
        m.message_id = a[1].trim();
        return m;
    }

    /**
     * This is to send the same message to multiple people. Only use this
     * function to send a maximum of 300 messages, and a minimum of 2.
     *
     * @param numbers The array of numbers that are to be sent to. They should be in international format.
     * @param message The message that you would like to send.
     * @return A message array, Each element will contain the number you sent to and the message ID or error per message.
     * @throws java.net.UnknownHostException This is thrown if the HOST cannot be found. Maybe you are not on the internet?
     * @throws Exception                     There are errors that get thrown, you should catch them.
     */
    public Message[] sendMessage(String[] numbers, String message)
            throws Exception {
        if (numbers.length < 2 || numbers.length > 300) {
            throw new Exception("Illegal arguments passed");
        }
        ArrayList<Message> messages = new ArrayList<Message>();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("user", this.userName));
        nameValuePairs.add(new BasicNameValuePair("api_id", this.apiId));
        nameValuePairs.add(new BasicNameValuePair("password", this.password));
        String numbersSend = numbers[0];
        for (int x = 1; x < numbers.length; x++) {
            numbersSend += "," + numbers[x];
        }
        nameValuePairs.add(new BasicNameValuePair("to", numbersSend));
        nameValuePairs.add(new BasicNameValuePair("text", message));

        // Send Request:
        String s = this.executePost(CLICKATELL_HTTP_BASE_URL + "sendmsg.php", nameValuePairs);
        // Check whether an auth failed happened:
        if (s.contains("Authentication failed")) {
            throw new Exception("Authentication Failed");
        }
        // We don't throw an exception here, as maybe only part of your
        // messages failed:
        String lines[] = s.split("\n");
        for (String l : lines) {
            String n[] = l.split(" To: ");
            Message m = new Message();
            m.number = n[1].trim();
            String q[] = n[0].split(": ");
            if (q[0].equalsIgnoreCase("err")) {
                m.error = q[1];
            } else {
                m.message_id = q[1];
            }
            messages.add(m);
        }
        return messages.toArray(new Message[0]);
    }

    /**
     * This will attempt to get the message status of a single message.
     *
     * @param messageId This is the message ID that you received when sending the
     *                  message.
     * @return Integer the status of the message.
     * @throws java.net.UnknownHostException This is thrown if the HOST cannot be found. Maybe you are not on the internet?
     * @throws Exception                     There are errors that get thrown, you should catch them.
     */
    public int getMessageStatus(String messageId) throws Exception {
        // Build Parameters:
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("user", this.userName));
        nameValuePairs.add(new BasicNameValuePair("api_id", this.apiId));
        nameValuePairs.add(new BasicNameValuePair("password", this.password));
        nameValuePairs.add(new BasicNameValuePair("apimsgid", messageId));

        // Send Request:
        String s = this.executePost(CLICKATELL_HTTP_BASE_URL + "querymsg.php", nameValuePairs);
        // Check whether an auth failed happened:
        if (s.contains("Authentication failed")) {
            throw new Exception("Authentication Failed");
        }
        // If there was an error, throw it.
        if (s.startsWith("ERR:")) {
            throw new Exception(s);
        }
        // We know the status will always be the fourth part:
        // Syntax: ID: xxx Status: xxx
        String[] a = s.split(" ");
        return Integer.parseInt(a[3].trim());
    }

    /**
     * This will get the status and charge of the message given by the
     * messageId.
     *
     * @param messageId The message ID that should be searched for.
     * @return A message object that will contain all the details that could be gleaned.
     * @throws java.net.UnknownHostException This is thrown if the HOST cannot be found. Maybe you are not on the internet?
     * @throws Exception                     There are errors that get thrown, you should catch them.
     */
    public Message getMessageCharge(String messageId) throws Exception {
        // Build Parameters:
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("user", this.userName));
        nameValuePairs.add(new BasicNameValuePair("api_id", this.apiId));
        nameValuePairs.add(new BasicNameValuePair("password", this.password));
        nameValuePairs.add(new BasicNameValuePair("password", this.password));
        nameValuePairs.add(new BasicNameValuePair("apimsgid", messageId));

        // Send Request:
        String s = this.executePost(CLICKATELL_HTTP_BASE_URL + "getmsgcharge.php", nameValuePairs);
        // Check whether an auth failed happened:
        if (s.contains("Authentication failed")) {
            throw new Exception("Authentication Failed");
        }
        Message m = new Message(messageId);
        // If there was an error, throw it.
        if (s.startsWith("ERR:")) {
            m.error = s.substring(4);
        } else {
            String[] a = s.split(" ");
            m.status = a[5].trim();
            m.charge = a[3].trim();
        }
        return m;
    }

    /**
     * This will try to stop a message that has been sent. Note that only
     * messages that are going to be sent in the future can be stopped. Or if by
     * some luck you message has not been sent to the operator yet.
     *
     * @param messageId The message ID that is to be stopped.
     * @return The current status of the message.
     * @throws java.net.UnknownHostException This is thrown if the HOST cannot be found. Maybe you are not on the internet?
     * @throws Exception                     There are errors that get thrown, you should catch them.
     */
    public int stopMessage(String messageId) throws Exception {
        // Build Parameters:
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("user", this.userName));
        nameValuePairs.add(new BasicNameValuePair("api_id", this.apiId));
        nameValuePairs.add(new BasicNameValuePair("password", this.password));
        nameValuePairs.add(new BasicNameValuePair("apimsgid", messageId));

        // Send Request:
        String s = this.executePost(CLICKATELL_HTTP_BASE_URL + "delmsg.php", nameValuePairs);
        // Check whether an auth failed happened:
        if (s.contains("Authentication failed")) {
            throw new Exception("Authentication Failed");
        }
        // If there was an error, throw it.
        if (s.startsWith("ERR")) {
            throw new Exception(s);
        }
        // Split the result we know that the status will always the fourth
        // part:
        // Format: ID: xxx Status: xxx
        String[] a = s.split(" ");
        return Integer.parseInt(a[3].trim());
    }

    /**
     * This does a coverage lookup on the given number.
     *
     * @param number The number that should be checked.
     * @return -1 for failure, or the minimum charge of the message.
     * @throws java.net.UnknownHostException This is thrown if the HOST cannot be found. Maybe you are not on the internet?
     * @throws Exception                     There are errors that get thrown, you should catch them.
     */
    public double getCoverage(String number) throws Exception {
        // Build Parameters:
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("user", this.userName));
        nameValuePairs.add(new BasicNameValuePair("api_id", this.apiId));
        nameValuePairs.add(new BasicNameValuePair("password", this.password));
        nameValuePairs.add(new BasicNameValuePair("msisdn", number));

        // Send Request:
        String s = this.executePost(CLICKATELL_UTILS_BASE_URL + "routecoverage.php", nameValuePairs);
        // Check whether an auth failed happened:
        if (s.contains("Authentication failed")) {
            throw new Exception("Authentication Failed");
        }
        if (s.startsWith("ERR")) {
            return -1;
        }
        String[] a = s.split("Charge: ");
        return Double.parseDouble(a[1]);
    }

    /**
     * This will allow you to use any feature of the API. Note that you can do
     * more powerful things with this function. And as such should only be used
     * once you have read the documentation, as the parameters are passed
     * directly to the API.
     *
     * @param numbers  The list of numbers that must be sent to.
     * @param message  The message that is to be sent.
     * @param features The extra features that should be included.
     * @return An array of Messages will be returned.
     * @throws java.net.UnknownHostException This is thrown if the HOST cannot be found. Maybe you are not on the internet?
     * @throws Exception                     There are errors that get thrown, you should catch them.
     */
    public Message[] sendAdvancedMessage(String[] numbers, String message,
                                         HashMap<String, String> features) throws Exception {
        ArrayList<Message> messages = new ArrayList<Message>();
        // Build Parameters:
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("user", this.userName));
        nameValuePairs.add(new BasicNameValuePair("api_id", this.apiId));
        nameValuePairs.add(new BasicNameValuePair("password", this.password));
        String number = numbers[0];
        for (int x = 1; x < numbers.length; x++) {
            number += "," + numbers[x];
        }
        nameValuePairs.add(new BasicNameValuePair("to", number));
        nameValuePairs.add(new BasicNameValuePair("text", message));

        // Send Request:
        for (Map.Entry<String, String> entry : features.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        // Send Request:
        String s = this.executePost(CLICKATELL_UTILS_BASE_URL + "routecoverage.php", nameValuePairs);
        // Check whether an auth failed happened:
        if (s.contains("Authentication failed")) {
            throw new Exception("Authentication Failed");
        }

        // This does some fancy swapping:
        String lines[] = s.split("\n");
        if (lines.length > 1) { // Sent more than one message
            for (String l : lines) {
                Message m = new Message();
                String i[] = l.split(" To: ");
                m.number = i[1];
                String n[] = i[0].split(": ");
                if (n[0].equalsIgnoreCase("err")) {
                    m.error = n[1];
                } else {
                    m.message_id = n[1];
                }
            }
        } else { // Sent one message
            String n[] = lines[0].split(": ");
            Message m = new Message();
            m.number = numbers[0];
            if (n[0].equalsIgnoreCase("err")) {
                m.error = n[1];
            } else {
                m.message_id = n[1];
            }
        }
        return messages.toArray(new Message[0]);
    }

    /**
     * This executes a POST query with the given parameters.
     *
     * @param targetURL      The URL that should get hit.
     * @param nameValuePairs The data you want to send via the POST.
     * @return The content of the request.
     * @throws java.net.UnknownHostException This is thrown if the HOST cannot be found. Maybe you are not on the internet?
     */
    private String executePost(String targetURL, List<NameValuePair> nameValuePairs) throws UnknownHostException {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(targetURL);

        try {
            // Add your data
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            return inputStreamToString(response.getEntity().getContent());

        } catch (UnknownHostException e) {
            throw e;
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        return "";
    }

    /**
     * This takes in the input stream and attempts to get the data into one stream and returns it.
     *
     * @param is The input stream.
     * @return The string of the entire input stream.
     * @throws IOException
     */
    private String inputStreamToString(InputStream is) throws IOException {
        String line;
        StringBuilder total = new StringBuilder();

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        // Read response until the end
        while ((line = rd.readLine()) != null) {
            total.append(line);
            total.append("\n");
        }

        // Return full string
        return total.toString().trim();
    }

    /**
     * This is the Message class that gets used as return values for some of the
     * functions.
     *
     * @author Dominic Schaff <dominic.schaff@gmail.com>
     *
     */
    public class Message {
        public String number = null, message_id = null, content = null,
                charge = null, status = null, error = null;

        public Message(String message_id) {
            this.message_id = message_id;
        }

        public Message() {
        }

        public String toString() {
            if (message_id != null) {
                return number + ": " + message_id;
            }
            return number + ": " + error;
        }
    }
}
