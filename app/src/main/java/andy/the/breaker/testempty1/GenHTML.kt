package andy.the.breaker.testempty1

import org.json.JSONObject
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

class GenHTML {
    fun genBasicHtml(input: List<Result>): String {
        if (input.isEmpty()) return "<p>No data provided.</p>"

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        val rows = input.joinToString("\n") { result ->
            val (date, time) = result.average_datetime.format(formatter).split(" ")
            val money = result.money

            val humanLookText = result.humanLook?.let { (title, json) ->
                "$title\n${json.toString(2)}"
            } ?: "No humanLook data"

            """
            <tr>
                <td>$time</td>
                <td>$date</td>
                <td>$money</td>
            </tr>
            <tr>
                <td colspan="3" class="scrollable">$humanLookText</td>
            </tr>
            """.trimIndent()
        }

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Generated HTML</title>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="https://cdn.datatables.net/1.13.4/css/jquery.dataTables.min.css">
                <style>
                    table { width: 100%; }
                    td.scrollable {
                        max-height: 20vh;
                        overflow-y: auto;
                        white-space: pre-wrap;
                    }
                </style>
            </head>
            <body>
                <table id="resultTable" class="display">
                    <thead>
                        <tr>
                            <th>Time</th>
                            <th>Date</th>
                            <th>Money</th>
                        </tr>
                    </thead>
                    <tbody>
                        $rows
                    </tbody>
                </table>
                <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
                <script src="https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js"></script>
                <script>
                    $(document).ready(function() {
                        $('#resultTable').DataTable({
                            paging: false,
                            searching: false,
                            info: false
                        });
                    });
                </script>
            </body>
            </html>
        """.trimIndent()
    }
}