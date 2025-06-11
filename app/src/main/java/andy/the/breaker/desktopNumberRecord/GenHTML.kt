package andy.the.breaker.desktopNumberRecord

import org.json.JSONObject
import java.time.format.DateTimeFormatter

class GenHTML {
    fun genBasicHtml(input: List<Result>): String {
        if (input.isEmpty()) return "<p>No data provided.</p>"

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        val rows = input.mapIndexed { index, result ->
            val (date, time) = result.average_datetime.format(formatter).split(" ")
            val money = result.money

            val (title, json) = result.humanLook ?: ("No humanLook title" to JSONObject())
            val jsonString = json.toString(2)
            val jsonId = "json-data-$index"

            """
    <tr>
        <td>$time</td>
        <td>$date</td>
        <td>$money</td>
    </tr>
    <tr>
        <td colspan="3" class="scrollable">
            <div>
                <strong style="cursor: pointer; color: blue;" onclick="toggleJson('$jsonId')">$title</strong>
                <pre id="$jsonId" class="hidden">$jsonString</pre>
            </div>
        </td>
    </tr>
    """.trimIndent()
        }.joinToString("\n")

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Generated HTML</title>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="https://cdn.datatables.net/1.13.4/css/jquery.dataTables.min.css">
                <!--     white-space: pre-wrap;-->
                <style>
                    table { width: 100%; }
                    td.scrollable {
                        height: 4vh;
                        max-height: 10vh;
                        overflow-y: auto;
                   
                    }
                    .hidden {
                        display: none;
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
                <script>
                    function toggleJson(id) {
                        const el = document.getElementById(id);
                        el.classList.toggle('hidden');
                    }
                </script>
            </body>
            </html>
        """.trimIndent()
    }
}