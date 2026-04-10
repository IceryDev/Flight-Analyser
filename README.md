# Flight Analyser by Still Processing (Group 13)

## Programming Project

### Team Members:
- Ulaş İçer (IceryDev)
- Zhou Sun (B33th0v3n960)
- Jessica Chen (jesschen8)
- Deea Zaharia (DeeaZaharia)
- Jagoda Koczwara-Szuba (jagoda-ks)
- Marco Fontana (nheatyon)


### Setup .env File:
- This project uses the OpenSky API, which requires a client ID and a client secret for requests.
- Head to the link [OpenSky](https://opensky-network.org/) and sign up/register.
- Click on "Reset Credential" to download the JSON file containing your client ID and client secret.
- Setup the .env file directly in the root folder of this project as follows:

```
# .env
CLIENT_ID=YOUR_CLIENT_ID
CLIENT_SECRET=YOUR_CLIENT_SECRET
```

## Compiling with Maven

On Macos or Linux, run the following command to create the `.jar file`.
```bash
./mvnw clean package
```

On Windows run this instead.
```bash
./mvnw.cmd clean package
```

The jar file should be in the `target` folder

---

To run the jar file you'll need to have `java` installed with `java` command in
your `PATH` environment variable.

Then run:
```bash
java -jar <path to jar file>
```

Make sure the `.env` is in the same directory you are running from.

## Running the Windows Release

Head to Releases tab and download the ZIP file. Extract all the files, fill in the environment variables with the instructions above in the .env file available within the folder.
