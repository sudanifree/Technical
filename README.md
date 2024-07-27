Here's a summary of key configuration settings and steps to ensure your Network Time Protocol (NTP) structure meets the requirements for a 2G network in Khartoum, Sudan:

### Summary of Key Configuration Settings
- **Home City**: Khartoum
- **Timezone**: Africa/Khartoum
- **Time Format**: 12-hour with AM/PM
- **Date Format**: `dd/mm/yyyy`
- **Daylight Saving**: Off
- **Update with Timezone**: On
- **Auto Update Time**: On

### Steps to Configure NTP

#### 1. Install NTP
Install NTP on your system:

- **For Debian/Ubuntu**:
  ```bash
  sudo apt update
  sudo apt install ntp
  ```

- **For CentOS/RHEL**:
  ```bash
  sudo yum install ntp
  ```

#### 2. Configure NTP

Edit the NTP configuration file at `/etc/ntp.conf`:

```plaintext
# /etc/ntp.conf, configuration for ntpd

# Use public NTP servers for Africa
server 0.africa.pool.ntp.org iburst
server 1.africa.pool.ntp.org iburst
server 2.africa.pool.ntp.org iburst
server 3.africa.pool.ntp.org iburst

# Set polling intervals
minpoll 6
maxpoll 10

# Restrict access to the local machine
restrict 127.0.0.1
restrict ::1

# Drift file to record frequency adjustments
driftfile /var/lib/ntp/ntp.drift

# Logfile configuration for monitoring
logfile /var/log/ntp.log
```

#### 3. Set Timezone
Set the system timezone to Khartoum:

```bash
sudo timedatectl set-timezone Africa/Khartoum
```

#### 4. Configure Date and Time Formats
Ensure the correct display of time in AM/PM format and date in `dd/mm/yyyy`:

**Create or edit `/etc/profile.d/date_time_format.sh`**:

```bash
# Set time format to 12-hour with AM/PM and date format to dd/mm/yyyy
alias date='date +"%I:%M %p, %d/%m/%Y"'
```

Apply the changes:

```bash
source /etc/profile.d/date_time_format.sh
```

#### 5. Enable Auto-Update for Time

Ensure that the system clock is synchronized with the NTP server on startup and periodically:

1. **Enable and Start NTP Service**:

   ```bash
   sudo systemctl enable ntp
   sudo systemctl start ntp
   ```

2. **Check NTP Status**:

   ```bash
   ntpq -p
   ```

#### 6. Set Daylight Saving Time
Since daylight saving time is not commonly used in Sudan, ensure it is set to off:

```bash
sudo timedatectl set-ntp true
sudo timedatectl set-timezone Africa/Khartoum
```

#### 7. Enable Auto Time Update and Timezone Updates

To ensure that the system time and timezone are automatically updated, enable these features through `timedatectl`:

```bash
sudo timedatectl set-ntp true
sudo timedatectl set-local-rtc 0
```

### Final Steps

1. **Restart NTP Service**:
   ```bash
   sudo systemctl restart ntp
   ```

2. **Verify Configuration**:
   ```bash
   timedatectl
   ntpq -p
   date
   ```

By following these steps, you will have configured a robust NTP setup tailored for a 2G network in Khartoum, ensuring accurate time synchronization with the specified time and date formats, timezone updates, and automatic time adjustments.
