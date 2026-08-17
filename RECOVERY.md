# Recovery — Puente

How to get a working signed (or debug) build back after a machine loss, bad checkout, or corrupted local tree. Full rebuild blueprints: [INTERNAL_REBUILD_GUIDE.md](INTERNAL_REBUILD_GUIDE.md).

Current product id: `com.puente.offline.translator`  
Kotlin namespace (unchanged): `com.example.offlinetranslator`  
Shipping version in Gradle: `versionName` 1.0 / `versionCode` 1

## 1) Restore source

```bash
git clone <repository-url>
cd Offline-translator
git checkout Emergent   # current product branch
```

Vosk models live in `app/src/main/assets/model-{en,es,fr,de}/`. Each folder **must** contain a `uuid` file or `StorageService.unpack()` fails.

## 2) Debug vs release

```bash
./gradlew :app:assembleDebug     # id: com.puente.offline.translator.debug
./gradlew :app:assembleRelease   # id: com.puente.offline.translator
```

Do **not** sell debug APKs or `*-unsigned.apk`.

Release is minified. Signing applies only if `keystore.properties` exists at the repo root.

## 3) Release keystore (do not lose this)

Git ignores `release.jks` and `keystore.properties`. Copy from `keystore.properties.example` and point `storeFile` at your `.jks`.

A local offline copy was created at:

```text
~/.puente-release-backup/release.jks
~/.puente-release-backup/keystore.properties
```

Copy that directory off this machine (USB / password manager). **If the key is lost, you cannot ship updates under the same application id.** Never commit the keystore or passwords.

```bash
cp keystore.properties.example keystore.properties
# fill storeFile / passwords / alias
./gradlew :app:assembleRelease
# APK: app/build/outputs/apk/release/app-release.apk
```

## 4) Device ids

| Build | applicationId |
| :--- | :--- |
| Release (Gumroad) | `com.puente.offline.translator` |
| Debug | `com.puente.offline.translator.debug` |
| Old debug suffix `PUENTE` | `com.puente.offline.translatorPUENTE` (obsolete; uninstall) |

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

## 5) First run after reinstall

1. Grant `RECORD_AUDIO`.
2. Complete onboarding (includes privacy notice).
3. Connect **Wi‑Fi** so ML Kit translation packs can download (`DownloadConditions.requireWifi()`).
4. Confirm Vosk model sizes on the main screen (EN/ES/FR/DE).

Privacy wording for buyers: [marketing/GUMROAD_LISTING.md](marketing/GUMROAD_LISTING.md). App license: [LICENSE](LICENSE). Third-party notices: [NOTICE](NOTICE).

## 6) If models will not unpack

- Check `app/src/main/assets/model-*/uuid` is packaged in the APK.
- Clear app storage and relaunch so unpack runs again.
- Do not strip `fst` / `mdl` / `ie` / `mat` / `dubm` compression exceptions in `app/build.gradle.kts`.

## 7) If release crashes at start

- Confirm `app/proguard-rules.pro` keeps Vosk, JNA, and ML Kit Language ID / Translate classes plus `ComponentRegistrar`s.
- Rebuild `assembleRelease` after ProGuard changes.

## Related

- [CHANGELOG.md](CHANGELOG.md)
- [README.md](README.md)
- [SETUP_GUIDE.md](SETUP_GUIDE.md)
