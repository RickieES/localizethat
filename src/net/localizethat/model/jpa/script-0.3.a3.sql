--
-- This Source Code Form is subject to the terms of the Mozilla Public
-- License, v. 2.0. If a copy of the MPL was not distributed with this
-- file, You can obtain one at http://mozilla.org/MPL/2.0/.
--

--------------------------------------------------------------------------------
-- SQL SCRIPT TO UPDATE DATABASE FROM 0.3.a2 TO 0.3.a3 IN A DERBY ENVIRONMENT --
--------------------------------------------------------------------------------

------------------------------------------------------------------------------
-- ADD FIELD L10N TO ALL LOCALENODE TABLES (LOCALECONTAINER, LOCALEFILE AND --
-- LOCALECONTENT)                                                           --
------------------------------------------------------------------------------

ALTER TABLE APP.LOCALECONTAINER
    ADD COLUMN "L10N_ID" int;

-- Add the L10N from LocalePath to the corresponding LocaleContainer
UPDATE APP.LOCALECONTAINER
    SET APP.LOCALECONTAINER.L10N_ID = (SELECT APP.LOCALEPATH.L10N_ID
        FROM APP.LOCALEPATH WHERE APP.LOCALEPATH.LCONTAINER_ID = APP.LOCALECONTAINER.ID)
    WHERE APP.LOCALECONTAINER.LNODEPARENT IS NULL;

-- Now we need to search for containers whose container parent has the L10N_ID set. At least
-- with my limited SQL knowledge, I can't do this in a recursive way, so the same query is
-- performed eight times to ensure that all containers are updated
UPDATE APP.LOCALECONTAINER AS L1
    SET L1.L10N_ID = (SELECT L2.L10N_ID
        FROM APP.LOCALECONTAINER AS L2
        WHERE L2.ID = L1.LNODEPARENT AND L2.L10N_ID IS NOT NULL)
    WHERE L1.L10N_ID IS NULL;

UPDATE APP.LOCALECONTAINER AS L1
    SET L1.L10N_ID = (SELECT L2.L10N_ID
        FROM APP.LOCALECONTAINER AS L2
        WHERE L2.ID = L1.LNODEPARENT AND L2.L10N_ID IS NOT NULL)
    WHERE L1.L10N_ID IS NULL;

UPDATE APP.LOCALECONTAINER AS L1
    SET L1.L10N_ID = (SELECT L2.L10N_ID
        FROM APP.LOCALECONTAINER AS L2
        WHERE L2.ID = L1.LNODEPARENT AND L2.L10N_ID IS NOT NULL)
    WHERE L1.L10N_ID IS NULL;

UPDATE APP.LOCALECONTAINER AS L1
    SET L1.L10N_ID = (SELECT L2.L10N_ID
        FROM APP.LOCALECONTAINER AS L2
        WHERE L2.ID = L1.LNODEPARENT AND L2.L10N_ID IS NOT NULL)
    WHERE L1.L10N_ID IS NULL;

UPDATE APP.LOCALECONTAINER AS L1
    SET L1.L10N_ID = (SELECT L2.L10N_ID
        FROM APP.LOCALECONTAINER AS L2
        WHERE L2.ID = L1.LNODEPARENT AND L2.L10N_ID IS NOT NULL)
    WHERE L1.L10N_ID IS NULL;

UPDATE APP.LOCALECONTAINER AS L1
    SET L1.L10N_ID = (SELECT L2.L10N_ID
        FROM APP.LOCALECONTAINER AS L2
        WHERE L2.ID = L1.LNODEPARENT AND L2.L10N_ID IS NOT NULL)
    WHERE L1.L10N_ID IS NULL;

UPDATE APP.LOCALECONTAINER AS L1
    SET L1.L10N_ID = (SELECT L2.L10N_ID
        FROM APP.LOCALECONTAINER AS L2
        WHERE L2.ID = L1.LNODEPARENT AND L2.L10N_ID IS NOT NULL)
    WHERE L1.L10N_ID IS NULL;

UPDATE APP.LOCALECONTAINER AS L1
    SET L1.L10N_ID = (SELECT L2.L10N_ID
        FROM APP.LOCALECONTAINER AS L2
        WHERE L2.ID = L1.LNODEPARENT AND L2.L10N_ID IS NOT NULL)
    WHERE L1.L10N_ID IS NULL;

ALTER TABLE APP.LOCALECONTAINER
    ALTER "L10N_ID" NOT NULL;



ALTER TABLE APP.LOCALEFILE
    ADD COLUMN "L10N_ID" int;

UPDATE APP.LOCALEFILE AS L1
    SET L1.L10N_ID = (SELECT L2.L10N_ID
        FROM APP.LOCALECONTAINER AS L2
        WHERE L2.ID = L1.LNODEPARENT AND L2.L10N_ID IS NOT NULL)
    WHERE L1.L10N_ID IS NULL;

ALTER TABLE APP.LOCALEFILE
    ALTER "L10N_ID" NOT NULL;



ALTER TABLE APP.LOCALECONTENT
    ADD COLUMN "L10N_ID" int;

UPDATE APP.LOCALECONTENT AS L1
    SET L1.L10N_ID = (SELECT L2.L10N_ID
        FROM APP.LOCALEFILE AS L2
        WHERE L2.ID = L1.LNODEPARENT AND L2.L10N_ID IS NOT NULL)
    WHERE L1.L10N_ID IS NULL;

ALTER TABLE APP.LOCALECONTENT
    ALTER "L10N_ID" NOT NULL;

UPDATE "APP"."CONFIG" SET CONFIGVALUE = '0.3.a3' WHERE ID = 'DB_VERSION';