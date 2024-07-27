To configure network settings on a Linux distribution, such as for setting up an APN, proxy, or other network details, follow these steps:

### Using NetworkManager (Graphical Interface)

1. **Open Network Settings:**
   - Navigate to your system's network settings through the system menu. This is usually found under "Settings" or "System Settings".

2. **Add New Connection:**
   - Click on "Add" or "Create" a new network connection. Choose the type of connection you want to configure (e.g., Mobile Broadband for APN settings).

3. **Enter Basic Settings:**
   - **Account name**: Enter a descriptive name for the connection.
   - **APN**: Enter the APN provided by your service provider.
   - **Username**: Enter the username if required.
   - **Password**: Enter the password if required.
   - **Auth. Type**: Choose "Normal" or "Secure" based on your provider’s requirements.

4. **Configure Advanced Settings:**
   - **Homepage**: Set the default homepage if applicable.
   - **Connection type**: Select "http".
   - **Use proxy**: Set to "no" or "yes" depending on whether you use a proxy.
   - **Proxy address**: Enter the proxy server address if using one.
   - **Proxy port**: Enter the proxy server port if using one.
   - **Proxy username**: Enter the proxy username if applicable.
   - **Proxy password**: Enter the proxy password if applicable.

5. **Save and Apply:**
   - Save the settings and apply the configuration. Connect to the network using the newly created profile.

### Using Command Line with `nmcli`

1. **Open Terminal:**
   - Open your terminal application.

2. **Add New Connection:**
   ```bash
   nmcli connection add type gsm ifname "*" con-name "MyInternetConnection" apn "internet.provider.com" user "username" password "password"
   ```

   Adjust `"MyInternetConnection"`, `"internet.provider.com"`, `"username"`, and `"password"` with your specific details.

3. **Configure Proxy (if applicable):**
   ```bash
   nmcli connection modify "MyInternetConnection" proxy.method manual proxy.address "proxy.example.com" proxy.port 8080
   ```

   Replace `"proxy.example.com"` and `8080` with your proxy server's address and port.

4. **Set Homepage and Other Advanced Settings:**
   Advanced settings like homepage are usually configured in the browser rather than through NetworkManager.

5. **Activate the Connection:**
   ```bash
   nmcli connection up "MyInternetConnection"
   ```

### Using `wvdial` for Mobile Broadband

1. **Install `wvdial`:**
   ```bash
   sudo apt-get install wvdial
   ```

2. **Configure `wvdial`:**
   - Open the configuration file for editing:
     ```bash
     sudo nano /etc/wvdial.conf
     ```

   - Add your settings:
     ```ini
     [Dialer Defaults]
     Init1 = ATZ
     Init2 = ATQ0 V1 E1 S0=0
     Init3 = AT+CGDCONT=1,"IP","internet.provider.com"
     Phone = *99#
     Username = username
     Password = password
     Stupid Mode = 1
     Auto Reconnect = on
     ```

   Replace `"internet.provider.com"`, `"username"`, and `"password"` with your details.

3. **Connect Using `wvdial`:**
   ```bash
   sudo wvdial
   ```

4. **Stop Connection:**
   - To disconnect, press `Ctrl + C` in the terminal where `wvdial` is running.

This configuration ensures you have set up your network connection correctly on a Linux distribution, whether using a graphical interface, `nmcli`, or `wvdial`.
