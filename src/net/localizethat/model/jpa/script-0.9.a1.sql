--
-- This Source Code Form is subject to the terms of the Mozilla Public
-- License, v. 2.0. If a copy of the MPL was not distributed with this
-- file, You can obtain one at http://mozilla.org/MPL/2.0/.
--

--------------------------------------------------------------------------------
-- SQL SCRIPT TO UPDATE DATABASE FROM 0.6.a1 TO 0.9.a1 IN A DERBY ENVIRONMENT --
--                                                                            --
-- This script does not add tables. It just adds constraints forgotten in     --
-- previous additions.                                                        --
--------------------------------------------------------------------------------

ALTER TABLE APP.LOCALECONTAINER
    ADD CONSTRAINT L10N_FK5
    FOREIGN KEY (L10N_ID)
    REFERENCES "APP"."L10N"(ID) ON DELETE CASCADE
;
ALTER TABLE APP.LOCALEFILE
    ADD CONSTRAINT L10N_FK6
    FOREIGN KEY (L10N_ID)
    REFERENCES "APP"."L10N"(ID) ON DELETE CASCADE
;
ALTER TABLE APP.LOCALECONTENT
    ADD CONSTRAINT L10N_FK7
    FOREIGN KEY (L10N_ID)
    REFERENCES "APP"."L10N"(ID) ON DELETE CASCADE
;

UPDATE "APP"."CONFIG" SET CONFIGVALUE = '0.9.a1' WHERE ID = 'DB_VERSION';
