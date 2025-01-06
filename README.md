# 🚀 Swipe Assignment.

## 📌 Features Implemented

✔️ Product Listing Screen – Displays all products with search functionality.

✔️ Add Product Screen – Allows users to enter a product name, type, price, tax, and upload an image.

✔️ Offline Support – Products added offline are saved locally and synced automatically when online.

✔️ Background Syncing – Uses WorkManager to sync offline data every 30 minutes.

✔️ Dependency Injection with Koin – Ensures scalable and modular code structure.

✔️ Modern Android Development – Implements MVVM, Repository Pattern, Coroutines, and Room Database.

✔️ Splash api to show splash screen on startUp.

## 🛠 Implementation Details

### 1️⃣ Product Listing Screen

Fetches products from the API using Retrofit.

Displays products using LazyColumn in Jetpack Compose.

Implements search functionality.

Displays a loading indicator while fetching data.

Does proper error handling using sealed class.

### 2️⃣ Add Product Screen

Users can enter product details (name, type, price, tax).

Allows image selection using the Android Photo Picker.

Data is validated before submission.

Uses POST API Request to submit product details.

### 3️⃣ Offline Functionality & WorkManager

Products created offline are stored in Room Database.

Whenever internet is available, the products get uploaded to server.

WorkManager is also implemented in case app is not open,
it runs every 30 minutes to sync unsynced products.

Ensures data consistency by deleting items from database once they are uploaded.

## 🔧 How to Run the Project

### Clone the Repository

git clone https://github.com/Priyanshu12122/Swipe-Assignment.git

## 👨‍💻 About Me

Developed by Priyanshu Kumar
📧 Email: kumar.priyanshu.yadav2@gmail.com
🔗 GitHub: https://github.com/Priyanshu12122

### Some Remarks

This is a fully functional Android app for managing products, built using Jetpack Compose for UI,
with offline support and automatic background synchronization using WorkManager.

This project is built entirely using Jetpack Compose, the modern UI toolkit for Android. While XML
has been the traditional approach for UI development, Jetpack Compose is the future of Android UI
development.

That being said, I am confident that I can learn XML within a few weeks if required, as UI
implementation is just one part of Android development. My expertise in MVVM, Coroutines, Room,
WorkManager, and Dependency Injection ensures that I can adapt quickly to any UI framework.

## 📢 Final Note

This project demonstrates my ability to build fully functional, scalable, and maintainable Android
applications. While Jetpack Compose is used for the UI, I am highly adaptable and can quickly learn
XML-based layouts if required. 🚀

Thank you for considering my submission! Let me know if you need further clarifications. 😊

