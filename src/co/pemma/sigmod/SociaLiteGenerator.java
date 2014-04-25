package co.pemma.sigmod;

import java.util.List;
import java.util.Map;

public class SociaLiteGenerator {

	/**
	 * Generate SociaLite code to load data into tables
	 * 
	 * @param tableName name of the table to generage SocialLite for
	 * @param colNames names of columns that we want to load
	 * @param schema SIGMOD db schema
	 * @return StringBuffer representing generated code
	 */
	public StringBuffer generateTables(String tableName, List<String> colNames, Map<String,List<String>> schema){
		StringBuffer sb = new StringBuffer();
		return sb;
	}
	
	/**
	 * Generate SociaLite code for SIGMOD query 3
	 * @return StringBuffer representing the generated code
	 */
	public StringBuffer generateQuery3(){
		StringBuffer sb = new StringBuffer();
		return sb;
	}
}
