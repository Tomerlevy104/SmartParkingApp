# Smart Parking App

An Android application for locating smart parking spots in the city. The app connects to the Smart Parking server and allows users to find available parking spots in real-time.

## 📌 Project Overview

The Smart Parking Client is an Android application designed for end users of the smart parking system. The application provides:

* **User management** - Registration, login, and profile updates
* **Parking spot location** - Display available parking spots in urban zones
* **Navigation** - Navigate to parking spots via Google Maps or Waze

The application connects to a Spring Boot server using REST API and displays real-time data.

## ⚙️ Technologies Used

* **Kotlin** - Primary programming language
* **Retrofit** - For HTTP communication with server

## 📁 Project Structure

```
app/src/main/
├── java/com/example/smartparkingapp/
│   ├── api/                    # API components and Retrofit
│   │   ├── ApiService.kt      # Endpoints definition
│   │   └── RetrofitClient.kt  # Retrofit client setup
│   │
│   ├── controller/            # Controller layer
│   │   ├── UserController.kt  # User controller
│   │   └── ObjectController.kt # Object controller
│   │
│   ├── model/                 # Data models
│   │   ├── UserModel.kt       # User model
│   │   ├── ParkingSpotModel.kt # Parking spot model
│   │   ├── UrbanZoneModel.kt  # Urban zone model
│   │   └── utils/             # Utilities and data structures
│   │
│   ├── services/              # Service layer
│   │   ├── IUserService.kt    # User service interface
│   │   ├── IObjectService.kt  # Object service interface
│   │   └── impl/              # Service implementations
│   │
│   ├── view/                  # UI components and Activities
│   │   ├── WelcomeActivity.kt     # Welcome screen
│   │   ├── LoginActivity.kt       # Login screen
│   │   ├── RegisterActivity.kt    # Registration screen
│   │   ├── UrbanZoneActivity.kt   # Main screen - urban zones
│   │   └── UserDetailsActivity.kt # User details screen
│   │
│   ├── NavigationUtils.kt     # Navigation utilities
│   ├── ObjectAndUserConverter.kt # Object and user conversion utilities
│   └── ValidationException.kt # Validation exception handling
│
├── res/
│   ├── layout/               # XML layout files
│   ├── values/              # Colors, strings, and styles
│   ├── drawable/            # Images and icons
│   └── xml/                 # Additional configurations
│
└── AndroidManifest.xml      # Application settings
```

## 📱 Application Screens

### 🏠 Welcome Screen (WelcomeActivity)
**Description**: Initial welcome screen with app logo and login/registration options

### 🔐 Login Screen (LoginActivity)
**Description**: Login screen with system ID and email fields

### 📝 Registration Screen (RegisterActivity)
**Description**: Registration screen with role selection and avatar choice

### 🏙️ Main Screen - Urban Zones (UrbanZoneActivity)
**Description**: Main screen displaying:
- List of urban zones
- Parking occupancy information
- List of available parking spots
- Navigation options

### 👤 User Details Screen (UserDetailsActivity)
**Description**: User profile editing screen - name, role, and avatar

## 🌐 Server Connection

The application connects to the server at: `http://YOUR_SERVER_IP:8081/`

### Updating Server Address
To change the server address, edit the `RetrofitClient.kt` file:

```kotlin
private const val BASE_URL = "http://YOUR_SERVER_IP:8081/"
```

### API Endpoints Used

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/ambient-intelligence/users` | Create new user |
| GET | `/ambient-intelligence/users/login/{systemID}/{userEmail}` | User login |
| PUT | `/ambient-intelligence/users/{systemID}/{userEmail}` | Update user data |
| GET | `/ambient-intelligence/objects/search/byType/{type}` | Search objects by type |
| POST | `/ambient-intelligence/commands` | Execute commands |

## 🚀 Setup and Installation

### Prerequisites
* Android Studio
* Kotlin 1.5.0 or newer
* Active Smart Parking Server

### Download and Installation

```bash
# Option 1: Clone the repository
git clone https://github.com/your-username/smart-parking-client.git
cd smart-parking-client

# Option 2: Download from GitHub
# 1. Go to the project page on GitHub
# 2. Click "Code" -> "Download ZIP"
# 3. Extract the ZIP file to your desired location

# Open in Android Studio
# File -> Open -> Select project folder
```

### Configuration

#### Update Server Address
Edit the file `app/src/main/java/com/example/smartparkingapp/api/RetrofitClient.kt`:

```kotlin
private const val BASE_URL = "http://YOUR_SERVER_IP:8081/"
```

#### Permission Settings
The `AndroidManifest.xml` file already contains all required permissions:
- Internet access
- Location access
- Access to navigation packages (Maps, Waze)

### Build and Run

1. Ensure Android device is connected or emulator is running
2. In Android Studio: **Run -> Run 'app'**
3. Or via command line:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

## 🎯 Key Features

### User Management
- **Registration**: Create new account with role selection (Admin/Operator/End User)
- **Login**: System login with email
- **Profile Update**: Change username, role, and avatar

### Parking Spot Search
- **Zone Viewing**: List of urban zones with occupancy data
- **Available Spots**: Display available parking spots in real-time
- **Parking Details**: Detailed information for each spot (address, restrictions, price)

### Navigation
- **Google Maps**: Direct navigation to parking spot
- **Waze**: Alternative navigation option
- **Auto Installation**: Guidance for installing navigation apps if needed

## 👤 Author

Made by Software Engineering students as part of a course project.