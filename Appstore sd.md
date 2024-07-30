لإنشاء نظام إلكتروني للرفع، العرض، وتحليل التطبيقات باستخدام PHP وMySQL، سأقدم لك كودًا كاملاً يتضمن العناصر الأساسية المطلوبة:

### 1. **قاعدة البيانات**

**إنشاء جداول قاعدة البيانات:**

```sql
CREATE DATABASE app_system;

USE app_system;

CREATE TABLE apps (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    file_link VARCHAR(255),
    upload_date DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    app_id INT,
    user_id INT,
    rating INT CHECK(rating >= 1 AND rating <= 5),
    review_text TEXT,
    FOREIGN KEY (app_id) REFERENCES apps(id)
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 2. **واجهة رفع التطبيقات**

**HTML - نموذج رفع التطبيقات:**

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload App</title>
</head>
<body>
    <h1>Upload an App</h1>
    <form action="upload_app.php" method="post" enctype="multipart/form-data">
        <label for="name">App Name:</label>
        <input type="text" id="name" name="name" required><br>
        <label for="description">Description:</label>
        <textarea id="description" name="description" required></textarea><br>
        <label for="file">App File (APK):</label>
        <input type="file" id="file" name="file" accept=".apk" required><br>
        <input type="submit" value="Upload">
    </form>
</body>
</html>
```

**PHP - معالجة رفع التطبيقات (`upload_app.php`):**

```php
<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "app_system";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $name = $_POST['name'];
    $description = $_POST['description'];
    $target_dir = "uploads/";
    $target_file = $target_dir . basename($_FILES["file"]["name"]);
    $uploadOk = 1;
    $fileType = strtolower(pathinfo($target_file, PATHINFO_EXTENSION));

    // Check if file is an APK
    if ($fileType != "apk") {
        echo "Only APK files are allowed.";
        $uploadOk = 0;
    }

    if ($uploadOk == 1) {
        if (move_uploaded_file($_FILES["file"]["tmp_name"], $target_file)) {
            $sql = "INSERT INTO apps (name, description, file_link) VALUES ('$name', '$description', '$target_file')";
            if ($conn->query($sql) === TRUE) {
                echo "The file has been uploaded and app details saved.";
            } else {
                echo "Error: " . $sql . "<br>" . $conn->error;
            }
        } else {
            echo "Sorry, there was an error uploading your file.";
        }
    }
}
$conn->close();
?>
```

### 3. **عرض التطبيقات**

**HTML - عرض قائمة التطبيقات:**

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Apps</title>
</head>
<body>
    <h1>Available Apps</h1>
    <?php
    $servername = "localhost";
    $username = "root";
    $password = "";
    $dbname = "app_system";

    // Create connection
    $conn = new mysqli($servername, $username, $password, $dbname);

    // Check connection
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    $sql = "SELECT id, name, description, file_link FROM apps";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            echo "<div class='app'>";
            echo "<h2>" . $row["name"]. "</h2>";
            echo "<p>" . $row["description"]. "</p>";
            echo "<a href='" . $row["file_link"]. "'>Download</a>";
            echo "</div>";
        }
    } else {
        echo "No apps available.";
    }
    $conn->close();
    ?>
</body>
</html>
```

### 4. **نظام تقييم التطبيقات**

**HTML - نموذج تقييم التطبيقات:**

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Submit Review</title>
</head>
<body>
    <h1>Submit a Review</h1>
    <form action="submit_review.php" method="post">
        <input type="hidden" name="app_id" value="app_id_value">
        <label for="rating">Rating:</label>
        <select name="rating" id="rating">
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="3">3</option>
            <option value="4">4</option>
            <option value="5">5</option>
        </select><br>
        <label for="review_text">Review:</label>
        <textarea id="review_text" name="review_text"></textarea><br>
        <input type="submit" value="Submit Review">
    </form>
</body>
</html>
```

**PHP - معالجة التقييمات (`submit_review.php`):**

```php
<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "app_system";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $app_id = $_POST['app_id'];
    $rating = $_POST['rating'];
    $review_text = $_POST['review_text'];
    $user_id = 1; // This should come from a user authentication system

    $sql = "INSERT INTO reviews (app_id, user_id, rating, review_text) VALUES ('$app_id', '$user_id', '$rating', '$review_text')";
    if ($conn->query($sql) === TRUE) {
        echo "Review submitted successfully.";
    } else {
        echo "Error: " . $sql . "<br>" . $conn->error;
    }
}
$conn->close();
?>
```

### 5. **تسجيل الدخول وإدارة المستخدمين**

**HTML - نموذج تسجيل الدخول:**

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
</head>
<body>
    <h1>Login</h1>
    <form action="login.php" method="post">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" required><br>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required><br>
        <input type="submit" value="Login">
    </form>
</body>
</html>
```

**PHP - معالجة تسجيل الدخول (`login.php`):**

```php
<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "app_system";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $username = $_POST['username'];
    $password = $_POST['password'];

    $sql = "SELECT id, password_hash FROM users WHERE username = '$username'";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        if (password_verify($password, $row['password_hash'])) {
            session_start();
            $_SESSION['user_id'] = $row['id'];
            header("Location: dashboard.php");
        } else {
            echo "Invalid username or password.";
        }
    } else {
        echo "Invalid username or password.";
    }
}
$conn->close();
?>
```

### 6. **تحليل البيانات وعرضها**

**PHP - تحليل البيانات وعرض التقارير:**

```php
<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "app_system";

// Create
