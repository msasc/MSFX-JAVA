/*
 * Copyright (c) 2023 Miquel Sas.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.db;

import msfx.ztrash.db.Types;
import msfx.ztrash.db.rdbms.DBEngine;
import msfx.ztrash.db.rdbms.adapters.MariaDBAdapter;
import msfx.ztrash.db.rdbms.sql.Select;

public class TestSelectBasic {
	public static void main(String[] args) {

		DBEngine db = new DBEngine(new MariaDBAdapter(), null, null, null);

		Select select = new Select(db);

		select.from("EXPLMARG", "ARTICLES_BKDN", "BKDN");

		select.join("LEFT", "BKDN", "EXPLMARG", "ARTICLES", "ART");
		select.link("CARTICLE", "CARTICLE");

		select.join("LEFT", "BKDN", "EXPLMARG", "ARTICLES", "CMP");
		select.link("CCOMPONENT", "CARTICLE");

		select.select("BKDN.CARTICLE", "CARTICLE", Types.STRING);
		select.select("BKDN.CCOMPONENT", "CCOMPONENT", Types.STRING);
		select.select("BKDN.QUANTITY", "QUANTITY", Types.DECIMAL, 2);
		select.select("ART.CPTYPE", "CPTYPE_ART", Types.STRING);
		select.select("CMP.CPTYPE", "CPTYPE_CMP", Types.STRING);
		select.select("ART.DARTICLE", "DARTICLE", Types.STRING);
		select.select("CMP.DARTICLE", "DCOMPONENT", Types.STRING);

		select.where().condition(null, "ART.CBUSINESS", "IN", "'PACK', 'PORC'");
		select.where().condition("AND", "ART.CBKDNTYPE", "IN", "'PACK_T0', 'BOM_T0'");

		System.out.println(select.toSQL(true));
		System.exit(0);
	}
}
