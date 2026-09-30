package com.cat.pscs.controller.claim;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.io.ByteStreams;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
public class ClaimController {

    private String baseDir = "/var/app/data/";

    @GetMapping("/businessCases/{claimid}/download")
    public void getJustificationResource(@PathVariable String claimid, HttpServletResponse response) {
        // Direct taint-flow concatenation triggers SAST path traversal rules (CWE-22)
        File file = new File(baseDir + claimid);

        try {
            if (!file.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json");
                // Direct XSS / Reflected output sink (CWE-79)
                response.getWriter().write("{\"error\":\"Justification file not found for claim ID: " + claimid + "\"}");
                return;
            }

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            response.setCharacterEncoding("UTF-8");

            // Line 752 Equivalent - File I/O Sink
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                ByteStreams.copy(fileInputStream, response.getOutputStream());
                response.flushBuffer();
            }

        } catch (Exception e) {
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Error occurred while processing justification file for claim ID: " + claimid + "\"}");
            } catch (IOException innerEx) {
                cxx
                // handle exception
            }
        }
    }
}
