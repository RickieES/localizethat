--
-- This Source Code Form is subject to the terms of the Mozilla Public
-- License, v. 2.0. If a copy of the MPL was not distributed with this
-- file, You can obtain one at http://mozilla.org/MPL/2.0/.
--

--------------------------------------------------------------------------------
-- SQL SCRIPT TO UPDATE DATABASE FROM 0.3.a1 TO 0.3.a2 IN A DERBY ENVIRONMENT --
--------------------------------------------------------------------------------

-----------------------------------------------------------------------
--                         TABLE LOCALECONTENT                       --
--                                                                   --
-- WARNING: BEFORE MODIFYING THIS TABLE, CHECK LOCALECONTENT AND ITS --
--          DESCENDENTS, SINCE THE LATTER EXTEND THE FORMER          --
-----------------------------------------------------------------------

CREATE TABLE "APP"."LOCALECONTENT"
(
    ID int CONSTRAINT LOCALECONTENT_PK PRIMARY KEY NOT NULL,
    ENTITYVERSION int,
    LC_TYPE VARCHAR(32),
    LNODENAME VARCHAR(128) NOT NULL,
    LNODEPARENT int,
    LNODETWIN int,
    LCONTENTDONTEXPORT boolean NOT NULL,
    LCONTENTORDERINFILE int,
    LCONTENTTEXTVALUE VARCHAR(32672),
    LCONTENTCOMMENT int,
    LCOMMENTTYPE VARCHAR(20),
    LNODECREATIONDATE timestamp,
    LNODELASTUPDATE timestamp
)
;
ALTER TABLE "APP"."LOCALECONTENT"
    ADD CONSTRAINT PARENT3_FK
    FOREIGN KEY (LNODEPARENT)
    REFERENCES "APP"."LOCALEFILE"(ID) ON DELETE CASCADE
;
ALTER TABLE "APP"."LOCALECONTENT"
    ADD CONSTRAINT TWIN3_FK
    FOREIGN KEY (LNODETWIN)
    REFERENCES "APP"."LOCALECONTENT"(ID) ON DELETE CASCADE
;
CREATE INDEX IDX_NODENAME3 ON "APP"."LOCALECONTENT"(LNODENAME)
;
CREATE INDEX IDX_PARENTNAME3 ON "APP"."LOCALECONTENT"(LNODEPARENT, LNODENAME)
;
INSERT INTO APP.COUNTERS (ENTITY, COUNTERVALUE)
	VALUES ('LOCALECONTENT', 0)
;

UPDATE "APP"."CONFIG" SET CONFIGVALUE = '0.3.a2' WHERE ID = 'DB_VERSION';
