# Private Lock
<a href="https://github.com/wesaphzt/privatelock/releases/latest" alt="GitHub Release"><img src="https://img.shields.io/github/release/wesaphzt/privatelock.svg?logo=github"></a>
[![License](https://img.shields.io/github/license/wesaphzt/privatelock.svg)](LICENSE)

[<img alt="Get it on F-Droid" height="75" src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png">](https://f-droid.org/packages/com.wesaphzt.privatelock/)

[description]
A simple app to automatically lock your phone based on movement force, or the acceleration to be more accurate.

Private Lock can help protect your privacy and security by monitoring the accelerometer in the background and if the threshold is breached, lock the screen.

The main use cases are if your phone is taken while you're using it, or you drop your phone.
As an additional bonus, it can also help save wear and tear on your power button!

Inspired by the abandoned [PluckLockEx](https://github.com/0xFireball/PluckLockEx) project, which is based on [PluckLock](https://github.com/SyntaxBlitz/PluckLock).

<p align="center">
<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/01-main.png?raw=true" width="200" height="400"/> 

<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/02-test-sensitivity.png?raw=true" width="200" height="400"/> 
</p>

## Features
- Lock phone when accelerometer threshold is breached
- Animation to help test & visualize lock sensitivity
- Notification when service is running
- Start service on boot option
- Home screen widget
- Pause service for your defined length of time

## Requirements
Device admin permission for locking screen.

## Permissions
android.permission.RECEIVE_BOOT_COMPLETED (start on boot)

android.permission.WAKE_LOCK (start on boot)

android.permission.BIND_DEVICE_ADMIN (lock the screen)

android.permission.FOREGROUND_SERVICE (run the service)

android.permission.BIND_JOB_SERVICE (run the service)

## Issues
To contribute, or to report issues please use the [Issue Tracker](https://github.com/wesaphzt/privatelock/issues/).

### Known Issues
You will need to use your PIN code to unlock when phone lock is triggered, even when fingerprint/pattern unlock is enabled.
This is a system limitation and according to Google, a "feature, not a bug".

This is not an issue on rooted phones, however root isn't supported as of yet.

If you experience issues, please make sure battery optimizations are disabled for the app.

## Privacy
Free from ads and tracking.

## License
[GPL v3.0](LICENSE)

## Tips
If you find these apps useful, consider supporting me in some way in my mission to create simple, useful, privacy-oriented, open-source apps.

BTC: 1GCkvAg9oG79niQTbh6EH9rPALQDXKyHKK

LTC: LV687s3wVdhmLZyJMFxomJHdHFXeFAKT5R

ETH: 0x785a8804c85b88683a5cce5e53f60878831e5d03

XMR: 43Vijzdt3y42mmT954rSYPjXYabDsjYEV2KyhxfC46JibR2ny9VmRS1fjdJTHxxPVPFE8ajgArwjWfyaRgjh9vcNAwmkfJj
