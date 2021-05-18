package com.raikun.mqtttest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Policy;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import static com.raikun.mqtttest.Channel.CHANNEL_1_ID;


public class MainActivity extends AppCompatActivity {

    static String MQTTHOST = "ssl://mqtt.iot.com.ph:8883";
    static String USERNAME = "iotadmin";
    static String PASSWORD = "P@55w0rd";

    private NotificationManagerCompat notificationManager;

    Button connect, pub, sub;
    EditText messages, topics, receiver;
    TextView submessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connect = findViewById(R.id.btnconnect);
        pub = findViewById(R.id.btnpub);
        sub = findViewById(R.id.btnsub);
        submessage = findViewById(R.id.submessage);
        messages = findViewById(R.id.edtpub);
        topics = findViewById(R.id.edtsub);
        receiver = findViewById(R.id.topictosend);

        String clientId = MqttClient.generateClientId();
        MqttAndroidClient client =
                new MqttAndroidClient(MainActivity.this, MQTTHOST,
                        clientId);
        


        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    MqttConnectOptions options = new MqttConnectOptions();
                    options.setUserName(USERNAME);
                    options.setPassword(PASSWORD.toCharArray());

                    TrustManagerFactory trustManagerFactory = null;
                    try {
                        trustManagerFactory = TrustManagerFactory.getInstance(
                                TrustManagerFactory.getDefaultAlgorithm());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    try {
                        trustManagerFactory.init((KeyStore) null);
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    }
                    TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                    X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

                    SSLContext sslContext = null;
                    try {
                        sslContext = SSLContext.getInstance("TLSv1.2");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    try {
                        sslContext.init(null, new TrustManager[] { trustManager }, null);
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    }
                    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());


                    options.setSocketFactory(sslContext.getSocketFactory());

                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            System.out.println(exception);

                        }
                    });


                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = receiver.getText().toString().trim();
                String message = messages.getText().toString().trim();
                try {
                    client.publish(topic, message.getBytes(), 0, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                submessage.setText(new String(message.getPayload()));
                notificationManager = NotificationManagerCompat.from(MainActivity.this);

                if (new String(message.getPayload()).equals("On") & topic.equals("Switch")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                        String cameraId = null;
                        try {
                            cameraId = camManager.getCameraIdList()[0];
                            camManager.setTorchMode(cameraId, true);   //Turn ON
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }



                }

                if (new String(message.getPayload()).equals("Off") & topic.equals("Switch")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                        String cameraId = null;
                        try {
                            cameraId = camManager.getCameraIdList()[0];
                            camManager.setTorchMode(cameraId, false);   //Turn OFF
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent(MainActivity.this, SwitchStatus.class);


                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.ic_baseline_message_24)
                        .setContentTitle("MQTT Message")
                        .setContentText(message.toString())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE);

                notificationManager.notify(0, builder.build());

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = topics.getText().toString().trim();
                int qos = 0;
                try {
                    IMqttToken subToken = client.subscribe(topic, qos);
                    subToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // The message was published
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken,
                                              Throwable exception) {
                            // The subscription could not be performed, maybe the user was not
                            // authorized to subscribe on the specified topic e.g. using wildcards

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}