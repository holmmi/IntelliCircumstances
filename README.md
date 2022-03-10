# IntelliCircumstances
IntelliCircumstances is an Android application which is used to measure different weather circumstances (air pressure, humidity and temperature).

## Why this project was done?
This project was done as a part of sensor-based mobile applications course in Metropolia University of Applied Sciences. The course belongs to mobile solutions major.

## What is neded to use this application?
IntelliCircumstances is created to work with RuuviTag device. RuuviTag is a small Bluetooth low-energy (BLE) device that boradcasts weather circumstance information about every second. More information about RuuviTag and resellers can be found from [manufacturer website](https://ruuvi.com/). This application is intended to work with a physical Android device.

## How to run this application?
IntelliCircumstances is dependent on Bluetooth, external sensors (RuuviTag) and Firebase Realtime Database. In case there is no internet connection, it is not possible to read or write shared measurement data.
1. Clone this repository (`git clone git@github.com:holmmi/IntelliCircumstances.git`).
2. Make sure you have Android Studio Arctic Fox or higher installed on your system. Open the project folder you just cloned with Andrid Studio.
3. In case you want to use your own Firebase Realtime Database instance, set it up by following [these](https://firebase.google.com/docs/android/setup) instructions. The current database instance is running on a free plan without any costs.
4. Copy the Firebase database address and change it in `repository` package's `ShareRepository.kt` file if you setup your own Firebase. This is a configuration setting and it will be moved to `local.properties` in further versions.
5. Connect your physical Android device and make sure the [USB debugging](https://developer.android.com/studio/debug/dev-options) is enabled.
6. Choose your device from a device dropdown menu and run the application from the Android Studio's play button. Building the application might take for a while.

## How to get support?
Feel free to contact original authors Mikael Holm and Tiitus Telke of this project. Pull requests are welcome.
