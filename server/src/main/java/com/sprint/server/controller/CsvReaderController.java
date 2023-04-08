package com.sprint.server.controller;

import com.opencsv.CSVReader;
import com.sprint.common.response.HttpApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileReader;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class CsvReaderController {

    private static final Function<List<String>, String> convertListToCommaSepStr = (lst) -> lst.stream()
            .map(str -> String.format("'%s'", str)).collect(Collectors.joining(","));

    public Map<String, String> getFolioAndIsinMapping() {
        Map<String, String> map = new HashMap<>();

        try (FileReader filereader = new FileReader("/Users/upstox/Downloads/unique_mapping_isin.csv")) {
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            int i = 0;
            while ((nextRecord = csvReader.readNext()) != null) {
                if (i == 0) {
                    i++;
                    continue;
                }
                String isin = nextRecord[1];
                String folioNumber = nextRecord[2];

                map.put(folioNumber, isin);
            }
        } catch (Exception e) {
            log.error("[getFolioAndIsinMapping]: ERROR: {}", e);
        }

        return map;
    }

    @PostMapping("/convertSip")
    public HttpApiResponse convertSip() {
        List<String> queries = new ArrayList<>();
        List<String> orderIds = new ArrayList<>();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");

        try (FileReader filereader = new FileReader("/Users/upstox/Downloads/ENG-68362 Data.xlsx - s2 orders.csv")) {
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            boolean vals = true;
            int i = 0;
            while ((nextRecord = csvReader.readNext()) != null) {
                if (i == 0) {
                    i++;
                    continue;
                }

                Double newNav = 22.17;
                Double oldAmount = Double.valueOf(nextRecord[5]);
                Double oldOrderAmount = Double.valueOf(nextRecord[2]);
                Double oldUnits = Double.valueOf(nextRecord[3]);
                Double oldNav = Double.valueOf(nextRecord[4]);
                String referenceKey = nextRecord[5];
                String orderId = nextRecord[1];

                Double units = (oldUnits / oldNav) * newNav;

                String oldUpstoxSchemeId = "104387";
                String oldBseScheme = "DWGADI-DP";
                String oldRtaSchemeCode = "GADI";
                String oldIsin = "INF223J01AS2";

                // String oldUpstoxSchemeId = "104520";
                // String oldBseScheme = "PRGAD2-DP";
                // String oldRtaSchemeCode = "GAD2";
                // String oldIsin = "INF223J01NG0";

                String query = String.format(
                        "UPDATE `mutualfunds`.`orders` SET upstox_scheme_id = '104411', isin = 'INF223J01NL0' , bse_scheme = 'PRGTD1-GR' , rta_scheme_code= 'GTD1', units = '%s', nav = '%s' WHERE `order_id` = '%s';",
                        units, newNav, orderId);

                String rollback = String.format(
                        "--rollback UPDATE `mutualfunds`.`orders` SET upstox_scheme_id = '%s', isin = '%s' , bse_scheme = '%s' , rta_scheme_code= '%s', units = '%s', nav = '%s' WHERE `order_id` = '%s';",
                        oldUpstoxSchemeId, oldIsin, oldBseScheme, oldRtaSchemeCode, oldUnits, oldNav, orderId);

                orderIds.add(rollback);
                queries.add(query);
            }
        } catch (Exception e) {
            log.error("Error: {}", e);
        }
        // queries.add(String.format("-- rollback DELETE FROM `mutualfunds`.`orders` where `order_id` in (%s)",
        // convertListToCommaSepStr.apply(orderIds)));

        return new HttpApiResponse(Arrays.asList(queries, orderIds));
    }

    @PostMapping("/convert/sell")
    public HttpApiResponse sellConvert() {

        List<String> queries = new ArrayList<>();
        List<String> orderIds = new ArrayList<>();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");

        Map<String, String> isinMap = getFolioAndIsinMapping();

        try (FileReader filereader = new FileReader("/Users/upstox/Downloads/switch_out_new.csv")) {
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            boolean vals = true;
            int i = 0;
            while ((nextRecord = csvReader.readNext()) != null) {
                if (i == 0) {
                    i++;
                    continue;
                }
                String ucc = nextRecord[30];
                String folioNumber = nextRecord[8];
                String units = nextRecord[15];
                String amount = nextRecord[16];
                String orderId = ucc + "-" + UUID.randomUUID();
                String upstoxSchemeId = "104387";
                String nav = nextRecord[19];

                String isin = isinMap.get(folioNumber);
                String status = "completed";
                String dateTime = "2023-01-20 00:00:00";
                String stampDuty = nextRecord[17];
                String stt = nextRecord[23];
                String transactionId = nextRecord[12];

                String query = String.format(
                        "INSERT INTO `mutualfunds`.`orders` ( `ucc`,  `type` , `upstox_scheme_id` ,`order_id`, `order_amount`, `order_units`,  `units`, `nav`, `amount`, `isin`,  `folio_number`, `status`, `remarks`,  `created_at`, `updated_at`,`order_date`, `stamp_duty` , `stt`, `transaction_id` ) VALUES ( '%s','S', '%s','%s', '%s', '%s', '%s', '%s' , '%s' , '%s', '%s', '%s', ' ',  '%s', '%s', '%s', '%s', '%s', '%s');",
                        ucc, upstoxSchemeId, orderId, amount, units, units, nav, amount, isin, folioNumber, status,
                        dateTime, dateTime, dateTime, stampDuty, stt, transactionId);

                if (!isinMap.containsKey(folioNumber)) {
                    log.error("ISIN not present for folio number: {}, ucc: {} , skipping the query: {}", folioNumber,
                            ucc, query);
                    continue;
                }

                if (ucc.isEmpty()) {
                    log.warn("UCC not present for: {}", query);
                }

                orderIds.add(orderId);
                queries.add(query);
            }
        } catch (Exception e) {
            log.error("Error: {}", e);
        }
        queries.add(String.format("-- rollback DELETE FROM `mutualfunds`.`orders` where `order_id` in (%s)",
                convertListToCommaSepStr.apply(orderIds)));

        return new HttpApiResponse(queries);
    }

    @PostMapping("/convert")
    public HttpApiResponse convert() {

        List<String> queries = new ArrayList<>();
        List<String> orderIds = new ArrayList<>();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");

        try (FileReader filereader = new FileReader("/Users/upstox/Downloads/Switch In.xlsx - Transaction (1).csv")) {
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            boolean vals = true;
            int i = 0;
            while ((nextRecord = csvReader.readNext()) != null) {
                if (i == 0) {
                    i++;
                    continue;
                }
                String ucc = nextRecord[30];
                String folioNumber = nextRecord[8];
                String units = nextRecord[15];
                String amount = nextRecord[16];
                String orderId = ucc + "-" + UUID.randomUUID();
                String upstoxSchemeId = "104411";
                String nav = nextRecord[19];
                String isin = "INF223J01NL0";
                String status = "completed";
                String dateTime = "2023-01-20 00:00:00";
                String stampDuty = nextRecord[17];
                String stt = nextRecord[23];
                String transactionId = nextRecord[12];

                String query = String.format(
                        "INSERT INTO `mutualfunds`.`orders` ( `ucc`,  `type` , `upstox_scheme_id` ,`order_id`, `order_amount`, `order_units`,  `units`, `nav`, `amount`, `isin`,  `folio_number`, `status`, `remarks`,  `created_at`, `updated_at`,`order_date`, `stamp_duty` , `stt`, `transaction_id` ) VALUES ( '%s','B', '%s','%s', '%s', '%s', '%s', '%s' , '%s' , '%s', '%s', '%s', ' ',  '%s', '%s', '%s', '%s', '%s', '%s');",
                        ucc, upstoxSchemeId, orderId, amount, units, units, nav, amount, isin, folioNumber, status,
                        dateTime, dateTime, dateTime, stampDuty, stt, transactionId);
                orderIds.add(orderId);
                queries.add(query);
            }
        } catch (Exception e) {
            log.error("Error: {}", e);
        }

        queries.add(String.format("-- rollback DELETE FROM `mutualfunds`.`orders` where `order_id` in (%s)",
                convertListToCommaSepStr.apply(orderIds)));

        return new HttpApiResponse(queries);
    }
}
