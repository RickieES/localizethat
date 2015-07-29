--
-- This Source Code Form is subject to the terms of the Mozilla Public
-- License, v. 2.0. If a copy of the MPL was not distributed with this
-- file, You can obtain one at http://mozilla.org/MPL/2.0/.
--

--------------------------------------------------------------------------------
-- SQL SCRIPT TO UPDATE DATABASE FROM 0.3.a3 TO 0.6.a1 IN A DERBY ENVIRONMENT --
--------------------------------------------------------------------------------

------------------------------------------------------------------------------
-- ADD METADATA FIELDS TO LTCONTENT TABLE                                   --
------------------------------------------------------------------------------

DECLARE GLOBAL TEMPORARY TABLE SESSION.DUMMY (ID int) ON COMMIT PRESERVE ROWS NOT LOGGED;

ALTER TABLE APP.LOCALECONTENT
    ADD COLUMN "LCONTENTKEEPORIG" boolean;

ALTER TABLE APP.LOCALECONTENT
    ADD COLUMN "LCONTENTTRNSSTATUS" VARCHAR(16);

ALTER TABLE APP.LOCALECONTENT
    ADD COLUMN "LCONTENTAKEY" int;

ALTER TABLE APP.LOCALECONTENT
    ADD COLUMN "LCONTENTCKEY" int;

INSERT INTO SESSION.DUMMY
    SELECT L1.ID
        FROM APP.LOCALECONTENT AS L1, APP.LOCALECONTENT AS L2
        WHERE L1.LNODETWIN = L2.ID AND L1.LCONTENTTEXTVALUE = L2.LCONTENTTEXTVALUE;

UPDATE APP.LOCALECONTENT AS L1
    SET L1.LCONTENTKEEPORIG = TRUE
    WHERE L1.ID IN (SELECT ID
                        FROM SESSION.DUMMY);

DELETE FROM SESSION.DUMMY;

-- Add the L10N from LocalePath to the corresponding LocaleContainer
UPDATE APP.LOCALECONTENT
    SET APP.LOCALECONTENT.LCONTENTTRNSSTATUS = 'Untranslated'
    WHERE (APP.LOCALECONTENT.LCONTENTTEXTVALUE IS NULL OR
           APP.LOCALECONTENT.LCONTENTTEXTVALUE = '');

UPDATE "APP"."CONFIG" SET CONFIGVALUE = '0.6.a1' WHERE ID = 'DB_VERSION';
