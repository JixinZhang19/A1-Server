/**
 * @author Rebecca Zhang
 * Created on 2024-06-01
 */

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SkierServlet extends HttpServlet {

    private static final Gson gson = new Gson();
    private static final Pattern pattern = Pattern.compile("[0-9]*");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        String urlPath = req.getPathInfo();
        // 1. Validate url path
        if (!isUrlValid(urlPath)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            SkierOutDto skierOutDto = new SkierOutDto("Invalid inputs: url");
            res.getWriter().write(gson.toJson(skierOutDto));
            return;
        }
        String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        // 2. Validate request body
        if (!isRequestBodyValid(requestBody)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            SkierOutDto skierOutDto = new SkierOutDto("Invalid inputs: request body");
            res.getWriter().write(gson.toJson(skierOutDto));
            return;
        }
        // 3. Successful request
        res.setStatus(HttpServletResponse.SC_CREATED);
        SkierOutDto skierOutDto = new SkierOutDto("Write successful");
        res.getWriter().write(gson.toJson(skierOutDto));
    }

    /**
     * @param urlPath
     * @return boolean
     * @Description check if the url path is valid
     */
    private boolean isUrlValid(String urlPath) {
        // 1) Check if urlPath is empty
        if (urlPath == null || urlPath.isEmpty()) {
            return false;
        }
        // 2) Check if urlParts align with the API spec
        String[] urlParts = urlPath.split("/");
        if (urlParts.length != 8) return false;
        if (!urlParts[2].equals("seasons") || !urlParts[4].equals("days") || !urlParts[6].equals("skiers")) {
            return false;
        }
        if (!pattern.matcher(urlParts[1]).matches() || !pattern.matcher(urlParts[5]).matches() || !pattern.matcher(urlParts[7]).matches()) {
            return false;
        }
        int days = Integer.parseInt(urlParts[5]);
        return 1 <= days && days <= 366;
    }

    /**
     * @param requestBody
     * @return boolean
     * @Description check if the request body is valid
     */
    private boolean isRequestBodyValid(String requestBody) {
        // 1) Check if requestBody is empty
        if (requestBody == null || requestBody.isEmpty()) {
            return false;
        }
        // 2) Deserialize Json to Object and check if requestBody is in Json format
        SkierInDto skierInDto;
        try {
            skierInDto = gson.fromJson(requestBody, SkierInDto.class);
        } catch (JsonSyntaxException e) {
            return false;
        }
        // 3) Check if fields and attributes match
        return skierInDto.getTime() != null && skierInDto.getLiftID() != null;
    }

}
