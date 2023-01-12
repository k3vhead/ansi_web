package com.ansi.scilla.web.test.job;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.JobTag;
import com.ansi.scilla.common.jobticket.JobTagType;
import com.ansi.scilla.common.utils.AppUtils;

public class MakeTagSQL {

	private final String casePattern = "\tcase\n" + 
			"		when count($TAG$.tag_id) > 0 then '$TAG$ '\n" + 
			"		else ''\n" + 
			"		end as $TAG$";
	private final String joinPattern = "left outer join job_tag as $TAG$ on $TAG$.tag_id=job_tag_xref.tag_id and $TAG$.abbrev='$TAG$'";
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			String tagSql = makeTagSql(conn, "my_tags");
			Statement s = conn.createStatement();
			String sql = "select job.job_id, tag_count.tag_list\n" + 
					"from job\n" + 
					"left outer join (" + tagSql + ") as tag_count on tag_count.job_id=job.job_id\n" +
					"order by job.job_id desc";
			System.out.println(sql);
			ResultSet rs = s.executeQuery(sql);
			while ( rs.next() ) {
				System.out.println(rs.getInt("job_id") + "\t" + rs.getString("tag_list"));
			}
			rs.close();
		} finally {
			conn.rollback();
			conn.close();
		}
	}
	
	private String makeTagSql(Connection conn, String innerTableName) throws Exception {
			JobTag jobTag = new JobTag();
			jobTag.setTagType(JobTagType.EQUIPMENT.name());
			List<JobTag> tagList = JobTag.cast(jobTag.selectSome(conn));
			
			List<String> fieldList = new ArrayList<String>();
			List<String> caseStringList = new ArrayList<String>();
			List<String> joinStringList = new ArrayList<String>();
			for ( JobTag tag : tagList ) {
				fieldList.add(innerTableName + "." + tag.getAbbrev());
				caseStringList.add(casePattern.replaceAll("\\$TAG\\$", tag.getAbbrev()));
				joinStringList.add(joinPattern.replaceAll("\\$TAG\\$", tag.getAbbrev()));
			}
			
			StringBuffer sql = new StringBuffer();
			sql.append("select job_id, concat(" + StringUtils.join(fieldList, ",") + ") as tag_list\n");
			sql.append("from (\n" + 
					"	select job_tag_xref.job_id, \n");
			sql.append(StringUtils.join(caseStringList, ",\n") + "\n");
			sql.append("from job_tag_xref\n");
			sql.append(StringUtils.join(joinStringList, "\n") + "\n");
			sql.append("group by job_tag_xref.job_id) as " + innerTableName + "\n");

			return sql.toString();
	}
	
	
	
	public static void main(String[] args) {
		try {
			new MakeTagSQL().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
