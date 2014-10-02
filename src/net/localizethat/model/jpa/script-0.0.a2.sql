--
-- This Source Code Form is subject to the terms of the Mozilla Public
-- License, v. 2.0. If a copy of the MPL was not distributed with this
-- file, You can obtain one at http://mozilla.org/MPL/2.0/.
--

--------------------------------------------------------------------------------
-- SQL SCRIPT TO UPDATE DATABASE FROM 0.0.a1 TO 0.0.a2 IN A DERBY ENVIRONMENT --
--------------------------------------------------------------------------------


ALTER TABLE "APP"."GLOSSARY" ADD "ENTITYVERSION" int;
ALTER TABLE "APP"."GLSENTRY" ADD "ENTITYVERSION" int;
ALTER TABLE "APP"."GLSTRANSLATION" ADD "ENTITYVERSION" int;
ALTER TABLE "APP"."L10N" ADD "ENTITYVERSION" int;

UPDATE "APP"."CONFIG" SET CONFIGVALUE = '0.0.a2' WHERE ID = 'DB_VERSION';
