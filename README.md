# M10 vs CR7 Coin Toss — Android project

A native Java Android app prototype with a 9:16 full-screen HUD, live persistent counters, a continuous random coin flip, 3.6-second animation, pause/resume, reset, and importable local audio clips.

## Build the APK
1. Install Android Studio (current stable) and Android SDK Platform 35.
2. Open this folder (`messi_cr7_app`) in Android Studio.
3. Allow Gradle sync to finish.
4. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. The debug APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

The project uses Android Gradle Plugin 8.7.3 and Java; no third-party app libraries are required.

## Audio clips
The app intentionally does not bundle the viral interview/celebration recordings because the sources found online do not clearly establish permission to redistribute the original recordings inside an APK. On the app screen, tap **ADD MESSI AUDIO** and select an audio file containing “Qué mirás, bobo?”; tap **ADD SIUUU AUDIO** and select a suitable Ronaldo “Siuuu!” clip. The chosen file URIs are saved and the app plays them on each result. Use audio you have permission to use, especially for monetized streams.

## Portrait credits
The app loads real player photos from Wikimedia Commons when online:
- Lionel Messi photo by Bryan Berlin / WikiPortraits, `Lionel Messi Argentina v Spain 19 July 2026-076.jpg`, CC BY-SA 4.0: https://commons.wikimedia.org/wiki/File:Lionel_Messi_Argentina_v_Spain_19_July_2026-076.jpg
- Cristiano Ronaldo photo by Bryan Berlin / WikiPortraits, `Cristiano Ronaldo Croatia v Portugal 2 July 2026-075 (cropped).jpg`, CC BY-SA 4.0: https://commons.wikimedia.org/wiki/File:Cristiano_Ronaldo_Croatia_v_Portugal_2_July_2026-075_(cropped).jpg

The app shows a simple M10/CR7 fallback avatar if images are unavailable. Photos are downloaded from Commons at runtime and therefore need an internet connection on first load.

## Notes
- Portrait lock and immersive mode are enabled for phone-screen recording.
- The score and round count persist across app launches; RESET clears them.
- The result is a fair 50/50 random selection, independent of the coin animation.
- Player names and catchphrases are used for fan-made entertainment. The app is not affiliated with or endorsed by either player.

## Sound clip pages
Use the page's **Download** control to save an audio file, then import it inside the app. These are community-posted meme clips; check the page's terms and the uploader's permissions before using them in a monetized stream.
- Messi “Qué mirás, bobo?”: https://www.voicy.network/sounds/D_s8umEz6EqBlSoB4DPztg-que-miras-bobo
- Ronaldo “Siuuu!”: https://www.voicy.network/sounds/rcC_5KlG3kqCfxoN26061A-siuuuuu

## Build APK in GitHub Actions (no local Android SDK needed)
A workflow is included at `.github/workflows/build-apk.yml`. Upload this project to a GitHub repository, open **Actions → Build Android APK → Run workflow**, and download the `Messi-CR7-Coin-Toss-APK` artifact from the completed run. This still requires your GitHub account to run the build; this chat environment cannot run Android's build toolchain itself.
