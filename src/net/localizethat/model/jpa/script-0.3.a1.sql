--
-- This Source Code Form is subject to the terms of the Mozilla Public
-- License, v. 2.0. If a copy of the MPL was not distributed with this
-- file, You can obtain one at http://mozilla.org/MPL/2.0/.
--

--------------------------------------------------------------------------------
-- SQL SCRIPT TO UPDATE DATABASE FROM 0.0.a3 TO 0.3.a1 IN A DERBY ENVIRONMENT --
--------------------------------------------------------------------------------

----------------------------------------------------------------------
--                         TABLE LOCALEFILE                         --
--                                                                  --
-- WARNING: BEFORE MODIFYING THIS TABLE, CHECK ABSTRACTLOCALENODE,  --
--          LOCALEFILE AND ITS DESCENDENTS, SINCE THE LATTER EXTEND --
--          THE FORMER                                              --
----------------------------------------------------------------------

CREATE TABLE "APP"."LOCALEFILE"
(
    ID int CONSTRAINT LOCALEFILE_PK PRIMARY KEY NOT NULL,
    ENTITYVERSION int,
    LF_TYPE VARCHAR(32),
    LNODENAME VARCHAR(128) NOT NULL,
    LNODEPARENT int,
    LNODETWIN int,
    LFILEDONTEXPORT boolean NOT NULL,
    LFILEMD5HASH VARCHAR(32),
    LNODECREATIONDATE timestamp,
    LNODELASTUPDATE timestamp
)
;
ALTER TABLE "APP"."LOCALEFILE"
    ADD CONSTRAINT PARENT2_FK
    FOREIGN KEY (LNODEPARENT)
    REFERENCES "APP"."LOCALECONTAINER"(ID) ON DELETE CASCADE
;
ALTER TABLE "APP"."LOCALEFILE"
    ADD CONSTRAINT TWIN2_FK
    FOREIGN KEY (LNODETWIN)
    REFERENCES "APP"."LOCALEFILE"(ID) ON DELETE CASCADE
;
CREATE INDEX IDX_NODENAME2 ON "APP"."LOCALEFILE"(LNODENAME)
;
CREATE INDEX IDX_PARENTNAME2 ON "APP"."LOCALEFILE"(LNODEPARENT, LNODENAME)
;
INSERT INTO APP.COUNTERS (ENTITY, COUNTERVALUE)
	VALUES ('LOCALEFILE', 0)
;

CREATE TABLE "APP"."LFILELOBS"
(
    ID int CONSTRAINT LFILELOBS_PK PRIMARY KEY NOT NULL,
    LFILECLOB CLOB(512K),
    LFILEBINARYCONTENT BLOB(1M)
)
;

UPDATE "APP"."CONFIG" SET CONFIGVALUE = '0.3.a1' WHERE ID = 'DB_VERSION';
