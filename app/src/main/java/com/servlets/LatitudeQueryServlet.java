package com.servlets;

import com.data.Cases;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
* Input: query.
* Output: a JSON object.
*/
@WebServlet("/latitude-query")
public class LatitudeQueryServlet extends HttpServlet {

  // A map of <latitude, num. cases>.
  private LinkedHashMap<Long, Cases> casesByLatitude;

  @Override
  public void init() {
    // Query cases vs temperature for the last one week.
    String atitudeQuery = 
      "SELECT "
      + "  CAST(ROUND(latitude) AS INT64) AS key, "
      + "  SUM(new_confirmed) AS confirmed, "
      + "  SUM(new_deceased) AS deceased, "
      + "  SUM(new_recovered) AS recovered, "
      + "  SUM(new_tested) AS tested "
      + "FROM "
      + "  `bigquery-public-data.covid19_open_data.covid19_open_data` "
      + "WHERE date BETWEEN DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY) AND DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY) "
      + "GROUP BY 1 "
      + "ORDER BY 1"; // Need to order the data so that they are connected in order in the line charts.
    QueryServletHelper helper = new QueryServletHelper();
    casesByLatitude = helper.getQueryData(atitudeQuery);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = gson.toJson(casesByLatitude);
    response.getWriter().println(json);
  }
}