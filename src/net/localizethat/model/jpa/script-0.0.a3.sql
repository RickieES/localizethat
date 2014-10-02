--
-- This Source Code Form is subject to the terms of the Mozilla Public
-- License, v. 2.0. If a copy of the MPL was not distributed with this
-- file, You can obtain one at http://mozilla.org/MPL/2.0/.
--

--------------------------------------------------------------------------------
-- SQL SCRIPT TO UPDATE DATABASE FROM 0.0.a2 TO 0.0.a3 IN A DERBY ENVIRONMENT --
--------------------------------------------------------------------------------

-------------------
-- TABLE CHANNEL --
-------------------

CREATE TABLE "APP"."CHANNEL"
(
   ID int CONSTRAINT CHANNEL_PK PRIMARY KEY NOT NULL,
   ENTITYVERSION int,
   CHNLNAME varchar(32) NOT NULL,
   CHNLDESCRIPTION long varchar,
   CHNLREPLACEMENTTAG varchar(16),
   CHNLREPLACEMENTTEXT varchar(64),
   CHNLCREATIONDATE timestamp,
   CHNLLASTUPDATE timestamp
)
;
CREATE UNIQUE INDEX IDX_CHNLNAME ON "APP"."CHANNEL"(CHNLNAME)
;
INSERT INTO APP.COUNTERS (ENTITY, "VALUE")
	VALUES ('CHANNEL', 0)
;

