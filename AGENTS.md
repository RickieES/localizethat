# AGENTS.md

LocalizeThat! — Swing localization tool for Mozilla software. Java SE 8 app, JPA (EclipseLink) over Derby embedded database.

## Build system

- **NetBeans + Ant project** (`nbproject/*`, `build.xml`). There is **no Maven/Gradle, no package manager, no CI**.
- Build from NetBeans ("Clean and Build") or run `ant` from the repo root (`build.xml` imports `nbproject/build-impl.xml`). `ant`/JDK must support `-source/-target 1.8`.
- `nbproject/build-impl.xml`, `genfiles.properties`, `project.properties`, `project.xml` are tracked; `nbproject/private/` is machine-specific and ignored.
- Output goes to `build/` and `dist/localizethat.jar` (both gitignored).

## Dependencies

- Third-party jars are **vendored under `lib/`** and wired through `lib/nblibraries.properties` (NetBeans library definitions) plus `javac.classpath` in `nbproject/project.properties`. To add a dependency: drop the jar in `lib/`, define a `libs.<name>.classpath` entry, and reference it in `project.properties`. Do not invent Maven-style resolution.
- Source/target is **Java 8** (`javac.source=1.8`) even though the platform is a newer JDK — write Java 8-compatible code. Build uses `-Xlint:unchecked` with deprecation warnings on.

## Entry point & GUI

- Main class: `net.localizethat.Main` (`src/net/localizethat/Main.java`); app version constant lives there (`Main.version`).
- Swing desktop app. Panels/dialogs under `net.localizethat.gui.*` are NetBeans form-designer pairs: `.form` (XML design) + generated `.java`. 20 such pairs exist. Editing the generated `.java` by hand works, but keep the matching `.form` in sync (regenerate via NetBeans, not by hand).
- `src/doc/*.puml` are PlantUML design docs.

## Database (important)

- `persistence.xml` (`src/META-INF/`) nominally targets Derby Network Server, but `Main.java` **overrides** the connection at runtime to use the **embedded** Derby driver with `jdbc:derby:lt-data` under `<user.dir>/db`. `src/META-INF/persistence.xml` lists every JPA entity class explicitly — add new entities there.
- The database is created and migrated on first run by `DBChecker` (`src/net/localizethat/system/DBChecker.java`): it applies the ordered `script-*.sql` files in `src/net/localizethat/model/jpa/` whose version is newer than `APP.CONFIG` `DB_VERSION` (versions are like `0.9.a1`). For a schema change, **add a new `script-X.Y.aN.sql`**; never edit scripts already applied. Existing DBs are not auto-upgraded on old rows (only version-gated scripts run).
- Derby embedded locks the DB — a second running instance fails with `XJ040/XSDB6`.
- `db/` and `localizethat.properties` are runtime state (gitignored). `localizethat.properties` is auto-created with defaults (e.g. `db.path=<user.dir>/db`, `sa`/`sa` login) by `AppSettings`.

## Tests

- Test source root is `test` with JUnit 4 (`junit-4.10.jar`) on the test classpath, but **no tests exist in the repo**. There is no test framework to discover; `ant test` would run JUnit only if a `test/` dir is added.

## Conventions

- Every source file carries an **MPL 2.0 license header** — copy it when creating new files.
- Code style: Javadoc on classes/methods, `@author rpalomares` tags.
