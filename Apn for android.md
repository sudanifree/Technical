To create and configure an APN in a Linux distribution, you typically need to work with NetworkManager or wvdial for mobile broadband connections. Here is a step-by-step guide on how to configure the settings using `nmcli`, a command-line tool for NetworkManager:

### Step-by-Step Guide to Configure APN in Linux:

1. **Open a Terminal**:
   - Open your terminal.

2. **Create a New Connection**:
   - Use `nmcli` to create a new mobile broadband connection with the specified settings.

3. **Configure the Connection**:
   - Set the name, APN, username, password, authentication type, APN type, and protocol.

### Command Example Using `nmcli`:

```sh
# Create a new connection
nmcli connection add type gsm ifname "*" con-name MyInternetConnection apn internet.provider.com

# Set the username and password
nmcli connection modify MyInternetConnection gsm.username user123
nmcli connection modify MyInternetConnection gsm.password pass123

# Set the authentication type
nmcli connection modify MyInternetConnection gsm.allowed-auth pap,chap

# Set the APN type
nmcli connection modify MyInternetConnection gsm.apn-type default,supl

# Set the APN protocol
nmcli connection modify MyInternetConnection connection.autoconnect yes
nmcli connection modify MyInternetConnection ipv4.method auto

# Save and activate the connection
nmcli connection up MyInternetConnection
```

### Step-by-Step Explanation:

1. **Create a New Connection**:
   ```sh
   nmcli connection add type gsm ifname "*" con-name MyInternetConnection apn internet.provider.com
   ```
   This command creates a new GSM connection named `MyInternetConnection` with the specified APN.

2. **Set the Username and Password**:
   ```sh
   nmcli connection modify MyInternetConnection gsm.username user123
   nmcli connection modify MyInternetConnection gsm.password pass123
   ```
   These commands set the username and password for the connection.

3. **Set the Authentication Type**:
   ```sh
   nmcli connection modify MyInternetConnection gsm.allowed-auth pap,chap
   ```
   This command configures the allowed authentication types (PAP or CHAP).

4. **Set the APN Type**:
   ```sh
   nmcli connection modify MyInternetConnection gsm.apn-type default,supl
   ```
   This command sets the APN type to `default` and `supl`.

5. **Set the APN Protocol**:
   ```sh
   nmcli connection modify MyInternetConnection connection.autoconnect yes
   nmcli connection modify MyInternetConnection ipv4.method auto
   ```
   These commands configure the connection to use IPv4 and enable auto-connect.

6. **Save and Activate the Connection**:
   ```sh
   nmcli connection up MyInternetConnection
   ```
   This command activates the newly created connection.

By following these steps, you should be able to configure and activate a mobile broadband connection on a Linux distribution using the specified APN settings.
