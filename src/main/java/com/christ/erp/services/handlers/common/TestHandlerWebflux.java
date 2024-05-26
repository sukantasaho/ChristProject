package com.christ.erp.services.handlers.common;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCountryDBOWebflux;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import com.christ.erp.services.dto.common.MenuScreenDTO;
import com.christ.erp.services.dto.common.ModuleDTO;
import com.christ.erp.services.dto.common.ModuleSubDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.transactions.common.TestTransactionWebflux;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class TestHandlerWebflux {
    @Autowired
    TestTransactionWebflux testTransactionWebflux;


    public Mono<Map<Integer, String>> getCountryMap() {
        Mono<Map<Integer, String>> countryMap = testTransactionWebflux.getCountryList()
                .flatMapMany(Flux::fromIterable)
                //.filter(s->s.getCountryName().toLowerCase().startsWith("in"))
                .collect(Collectors.toMap(ErpCountryDBOWebflux::getId, ErpCountryDBOWebflux::getCountryName));
        return countryMap;
    }


    public Mono<String> getCountryMap1() {
        //testTransactionWebflux.getCountryList1().stream()
        //.sorted(Comparator.comparing(ErpCountryDBOWebflux::getCountryName))
        //.map(ErpCountryDBOWebflux::getCountryName) //Stream<String>
        //.map(String::toUpperCase)//Stream<String>
        //.anyMatch(s->s.equals("helloo"))
        //.noneMatch(s->s.startsWith("a"))
        //.allMatch(s->s.startsWith("a"))
        //.peek(System.out::println).count();


        return testTransactionWebflux.getCountryList()
                .flatMapMany(Flux::fromIterable)
                //.filter(s->s.getCountryName().toLowerCase().startsWith("in"))
                //.map(ErpCountryDBOWebflux::getCountryName)
                //.distinct()
                .sort(Comparator.comparing(ErpCountryDBOWebflux::getCountryName))
                .map(ErpCountryDBOWebflux::getCountryName)
                //.map(String::toUpperCase)
                //.doOnNext(this::firstOperation)
                //.doOnNext(this::secondOperation)
                //.map(this::firstOperation)
                //.map(this::secondOperation)
                //.collect(Collectors.toList());
                //.collect(Collectors.toSet());
                .collect(Collectors.joining("-","(",")"));
    }

    public ModuleDTO firstOperation(String str) {

        return new ModuleDTO();
    }

    public MenuScreenDTO secondOperation(ModuleDTO str) {

        return new MenuScreenDTO();
    }


    public Mono<List<ModuleDTO>> getScreenList() {
        //old sorting way
        List<ErpCountryDBOWebflux> li = new ArrayList<>();
        li.sort(new Comparator<ErpCountryDBOWebflux>() {
            @Override
            public int compare(ErpCountryDBOWebflux o1, ErpCountryDBOWebflux o2) {
                return 0;
            }
        });

        Mono<List<Object[]>> list = testTransactionWebflux.getMenuScreenList();
        Mono<Map<Integer, Map<Integer, List<Object[]>>>> map = list.flatMapMany(Flux::fromIterable) //List<Object[]> to Flux<Object[]>
                                                                .filter(objects -> !Utils.isNullOrEmpty(objects[1]) && !Utils.isNullOrEmpty(objects[4]))
                                                                .collect(Collectors.groupingBy(s -> Integer.parseInt(s[1].toString()),
                                                                            Collectors.groupingBy(r -> Integer.parseInt(r[4].toString()), Collectors.toList())));
        return map.map(this::convertObjectToDTO);
    }

    public List<ModuleDTO> convertObjectToDTO(Map<Integer, Map<Integer, List<Object[]>>> obj) {
        List<ModuleDTO> list = new ArrayList<>();
        obj.forEach((module,subModules) -> {
            ModuleDTO moduleDTO = new ModuleDTO();
            List<ModuleSubDTO> subDTOS = new ArrayList<>();
            subModules.forEach((submodule,menuScreens) -> {
                ModuleSubDTO subDTO = new ModuleSubDTO();
                List<MenuScreenDTO> menuScreenDTOS = new ArrayList<>();
                menuScreens.forEach(obj1 -> {
                    if (Utils.isNullOrEmpty(moduleDTO.id)) {
                        moduleDTO.setId(obj1[1].toString());
                        moduleDTO.setText(obj1[2].toString());
                        moduleDTO.setDisplayOrder(Integer.parseInt(obj1[0].toString()));
                    }
                    if (Utils.isNullOrEmpty(subDTO.ID)) {
                        subDTO.setID(obj1[4].toString());
                        subDTO.setText(obj1[5].toString());
                        subDTO.setDisplayOrder(Integer.parseInt(obj1[3].toString()));
                    }
                    MenuScreenDTO menuScreenDTO = new MenuScreenDTO();
                    menuScreenDTO.setID(obj1[7].toString());
                    menuScreenDTO.setText(obj1[8].toString());
                    menuScreenDTO.setDisplayOrder(Integer.parseInt(obj1[6].toString()));
                    menuScreenDTOS.add(menuScreenDTO);
                });

                menuScreenDTOS.sort(Comparator.comparing(MenuScreenDTO::getDisplayOrder));
                subDTO.Items = menuScreenDTOS;
                subDTOS.add(subDTO);
            });
            subDTOS.sort(Comparator.comparing(ModuleSubDTO::getDisplayOrder).thenComparing(ModuleSubDTO::getID));
            moduleDTO.menus = subDTOS;
            list.add(moduleDTO);
        });
        list.sort(Comparator.comparing(ModuleDTO::getDisplayOrder).thenComparing(ModuleDTO::getDisplayOrder));
        return list;
    }

    public void dateTimeFunctions() {
       //old method
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        System.out.println("old method "+year+"-"+month+"-"+day);

        //new method
        LocalDate localDate = LocalDate.now();
        System.out.println("getMonth "+localDate.getMonth());
        System.out.println("getMonthValue "+localDate.getMonthValue());
        System.out.println("getDayOfMonth "+localDate.getDayOfMonth());
        System.out.println("getDayOfYear "+localDate.getDayOfYear());
        System.out.println("getDayOfWeek "+localDate.getDayOfWeek());
        System.out.println("DAY_OF_MONTH "+localDate.get(ChronoField.DAY_OF_MONTH));
        System.out.println("isLeapYear "+localDate.isLeapYear());

        //Compare local dates, local time, Period of dates, Duration of time
        LocalDate localDate1 = LocalDate.of(2021,8,6);
        LocalDate localDate2 = LocalDate.of(2021,8,7);
        System.out.println("isBeforeDate "+ localDate1.isBefore(localDate2));
        System.out.println("isAfterDate "+ localDate1.isAfter(localDate2));
        System.out.println("isEqualDate "+ localDate1.isEqual(localDate2));
        Period period = localDate2.until(localDate1);
        System.out.println("period "+period);

        LocalTime localTime1 = LocalTime.of(11,20);
        LocalTime localTime2 = LocalTime.of(12,20);
        System.out.println("isBeforeTime "+ localTime1.isBefore(localTime2));
        System.out.println("isisAfterTime "+ localTime1.isAfter(localTime2));
        System.out.println("isEqualTime "+ localTime1.equals(localTime2));
        System.out.println("Duration of time "+ Duration.between(localTime1,localTime2));

        //convert local date or Local time to LocalDateTime, LocalDateTime to Local date or LocalTime
        LocalDateTime dateTimeMergeFromDate = localDate1.atTime(22,33);
        System.out.println("dateTimeMergeFromDate "+dateTimeMergeFromDate);
        System.out.println("toLocalDate "+ dateTimeMergeFromDate.toLocalDate());
        System.out.println("toLocalTime "+ dateTimeMergeFromDate.toLocalTime());

        LocalTime localTime = LocalTime.now();
        LocalDateTime dateTimeMergeFromTime = localTime.atDate(localDate1);
        System.out.println("dateTimeMergeFromTime "+dateTimeMergeFromTime);


        //Modify dates
        System.out.println("plusDays "+localDate.plusDays(1));
        System.out.println("plusMonths "+localDate.plusMonths(1));
        System.out.println("plusYears "+localDate.plusYears(1));
        System.out.println("minusDays "+localDate.minusDays(1));
        System.out.println("minusMonths "+localDate.minusMonths(1));
        System.out.println("minusYears "+localDate.minusYears(1));
        System.out.println("withYear "+localDate.withYear(2020));


        System.out.println("Current date " + localDate);





        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println("AMPM_OF_DAY "+dateTime.get(ChronoField.AMPM_OF_DAY));

        LocalDate localDate3 = LocalDate.ofYearDay(2018, 365);
        System.out.println("ofYearDay "+localDate3);



        //convert string to local date;
        String date = "06/08/2021";
        LocalDate localDate4 = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        System.out.println("String to localdate "+ localDate4);

        //convert local date to string
        LocalDate localDate5 = LocalDate.now();
        String date1 = localDate5.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        System.out.println("local date to String "+ date1);

        //convert local (date or time) to sql (date or time).vice versa
        Timestamp timestamp = java.sql.Timestamp.valueOf(dateTime);
        java.sql.Date dateSql = java.sql.Date.valueOf(localDate);
        LocalDate localDate6 = dateSql.toLocalDate();
        Time sqlTime = java.sql.Time.valueOf(localTime);
        LocalTime localTime3 = sqlTime.toLocalTime();
    }

}
