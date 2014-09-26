--
-- This Source Code Form is subject to the terms of the Mozilla Public
-- License, v. 2.0. If a copy of the MPL was not distributed with this
-- file, You can obtain one at http://mozilla.org/MPL/2.0/.
--

----------------------------------------------------------
-- SQL SCRIPT TO CREATE DATABASE IN A DERBY ENVIRONMENT --
----------------------------------------------------------


------------------
-- TABLE CONFIG --
------------------

CREATE TABLE "APP"."CONFIG" (
  ID varchar(32) CONSTRAINT CONFIG_PK PRIMARY KEY NOT NULL,
  VALUE varchar(128)
);
--
-- Database schema version. As config values are all strings, and for simplicity,
-- we deal with this value as a string in three chunks separated by dots:
--
-- The first chunk should match major versions of the application, parsed as an integer
-- The second chunk should match minor versions of the application, parsed as an integer
-- The third chunk may be used during development to trigger new updates of the
--     schema without changing the app version, parsed as a string like "a1" for alpha
--     versions, "b2" for beta versions, or "r5" for release maintenance version
INSERT INTO APP.CONFIG (ID, "VALUE")
	VALUES ('DB_VERSION', '0.0.a1')
;


--------------------
-- TABLE COUNTERS --
--------------------

CREATE TABLE "APP"."COUNTERS" (
  ENTITY varchar(32) CONSTRAINT COUNTERS_PK PRIMARY KEY NOT NULL,
  VALUE int
)
;
INSERT INTO APP.COUNTERS (ENTITY, "VALUE") 
	VALUES ('L10N', 0)
;
INSERT INTO APP.COUNTERS (ENTITY, "VALUE") 
	VALUES ('GLOSSARY', 0)
;

INSERT INTO APP.COUNTERS (ENTITY, "VALUE")
	VALUES ('GLSENTRY', 0)
;

INSERT INTO APP.COUNTERS (ENTITY, "VALUE")
	VALUES ('GLSTRANSLATION', 0)
;

----------------
-- TABLE L10N --
----------------

CREATE TABLE "APP"."L10N"
(
   ID int CONSTRAINT L10N_PK PRIMARY KEY NOT NULL,
   L10NCODE varchar(10) NOT NULL,
   L10NNAME varchar(80),
   L10NTEAMNAME varchar(80),
   L10NURL varchar(160),
   L10NCREATIONDATE timestamp,
   L10NLASTUPDATE timestamp
)
;
CREATE UNIQUE INDEX IDX_L10NCODE ON "APP"."L10N"(L10NCODE)
;
CREATE UNIQUE INDEX IDX_L10NNAME ON "APP"."L10N"(L10NNAME)
;


--------------------
-- TABLE GLOSSARY --
--------------------

CREATE TABLE "APP"."GLOSSARY"
(
   ID int CONSTRAINT GLOSSARY_PK PRIMARY KEY NOT NULL,
   GLOSNAME varchar(64) NOT NULL,
   GLOSVERSION varchar(10) NOT NULL,
   GLOSCREATIONDATE timestamp,
   GLOSLASTUPDATE timestamp,
   L10N_ID int NOT NULL
)
;
ALTER TABLE "APP"."GLOSSARY"
    ADD CONSTRAINT L10N_FK
    FOREIGN KEY (L10N_ID)
    REFERENCES "APP"."L10N"(ID) ON DELETE CASCADE
;
CREATE UNIQUE INDEX IDX_GLOSNAME ON "APP"."GLOSSARY"(GLOSNAME)
;
CREATE INDEX IDX_L10N_ID ON "APP"."GLOSSARY"(L10N_ID)
;


--------------------
-- TABLE GLSENTRY --
--------------------

CREATE TABLE "APP"."GLSENTRY"
(
   ID int CONSTRAINT GLSENTRY_PK PRIMARY KEY NOT NULL,
   GLSETERM varchar(64) NOT NULL,
   GLSECOMMENT long varchar,
   GLSEPARTOFSPEECH varchar(20),
   GLSECREATIONDATE timestamp,
   GLSELASTUPDATE timestamp,
   GLOS_ID int NOT NULL
)
;
ALTER TABLE "APP"."GLSENTRY"
    ADD CONSTRAINT GLOSSARY_FK
    FOREIGN KEY (GLOS_ID)
    REFERENCES "APP"."GLOSSARY"(ID) ON DELETE CASCADE
;
CREATE INDEX IDX_GLSETERM ON "APP"."GLSENTRY"(GLSETERM)
;
CREATE INDEX IDX_GLOS_ID ON "APP"."GLSENTRY"(GLOS_ID)
;
CREATE INDEX IDX_GLSEPARTOFSPEECH ON "APP"."GLSENTRY"(GLSEPARTOFSPEECH, GLSETERM)
;


--------------------------
-- TABLE GLSTRANSLATION --
--------------------------

CREATE TABLE "APP"."GLSTRANSLATION"
(
   ID int CONSTRAINT GLSTRANSLATION_PK PRIMARY KEY NOT NULL,
   GLSTVALUE varchar(64) NOT NULL,
   GLSTCOMMENT long varchar,
   GLSTCREATIONDATE timestamp,
   GLSTLASTUPDATE timestamp,
   GLSE_ID int NOT NULL,
   L10N_ID int NOT NULL
)
;
ALTER TABLE "APP"."GLSTRANSLATION"
    ADD CONSTRAINT GLSENTRY_FK
    FOREIGN KEY (GLSE_ID)
    REFERENCES "APP"."GLSENTRY"(ID) ON DELETE CASCADE
;
ALTER TABLE "APP"."GLSTRANSLATION"
    ADD CONSTRAINT L10N2_FK
    FOREIGN KEY (L10N_ID)
    REFERENCES "APP"."L10N"(ID) ON DELETE CASCADE
;
CREATE INDEX IDX_GLSTVALUE ON "APP"."GLSTRANSLATION"(GLSTVALUE)
;
CREATE INDEX IDX_GLSE_ID ON "APP"."GLSTRANSLATION"(GLSE_ID)
;
CREATE INDEX IDX_L10N_ID ON "APP"."GLSTRANSLATION"(L10N_ID)
;


