package com.christ.erp.services.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.Tuple;

import org.apache.tika.Tika;
import org.jsoup.Jsoup;
import org.springframework.http.codec.multipart.FilePart;

import com.christ.erp.services.dto.common.LookupItemDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

@SuppressWarnings({"unchecked","rawtypes"})
public class Utils {
    public static void log(String message) {
        try {
            String method = Thread.currentThread().getStackTrace()[2].getMethodName();
            if(method != null && method.trim().length() > 0) {

            }
        }
        catch(Exception ex) { }
    }
    public static boolean isNullOrEmpty(Object value) {
        return (value == null);
    }
    public static boolean isNullOrEmpty(String value) {
        return (value == null || value.trim().isEmpty());
    }
    public static <T> boolean isNullOrEmpty(Set<T> value) {
        return (value == null || value.isEmpty() || value.size() == 0);
    }
	public static boolean isNullOrEmpty(List value) {
        return (value == null || value.isEmpty() || value.size() == 0);
    }
	public static boolean isNullOrEmpty(Map value) {
        return (value == null || value.isEmpty() || value.size() == 0);
    }
    public static boolean isNullOrWhitespace(String value) {
        return ((Utils.isNullOrEmpty(value)) || value.trim().length() == 0);
    }
    public static boolean isNullOrEmpty(Integer value) {
        return (value == null || value == 0);
    }
    public static boolean isNullOrEmpty(Long value) {
        return (value == null || value == 0);
    }
    public static boolean isNullOrEmpty(Boolean value) {
        return (value == null);
    }
    public static String setDefaultIfEmpty(String value, String defaultValue) {
        return Utils.isNullOrEmpty(value) == true ? defaultValue : value;
    }
    public static String setDefaultIfWhitespace(String value, String defaultValue) {
        return Utils.isNullOrWhitespace(value) == true ? defaultValue : value;
    }
    public static boolean isNullOrEmpty(BigDecimal value) {
        return (value == null || value.compareTo(BigDecimal.ZERO) == 0);
    }
    
	public static ApiResult<List<LookupItemDTO>> getDropdownData(ApiResult<List<LookupItemDTO>> result, EntityManager context, String queryString, Map<String, ? extends Object> args){
    	Query query = context.createNativeQuery(queryString, Tuple.class);
    	if(args != null && !args.isEmpty()) {
    		if(args != null && args.size() > 0) {
                for(Entry<String, ? extends Object> entry : args.entrySet()) {
                    String parameter = entry.getKey();
                	Object value = entry.getValue();
                	if(value.getClass() == int.class || value.getClass() == Integer.class) {
                        query.setParameter(parameter, (Integer) value);
                    }
                    else if(value.getClass() == double.class || value.getClass() == Double.class) {
                        query.setParameter(parameter, (double) value);
                    }
                    else if(value.getClass() == boolean.class || value.getClass() == Boolean.class) {
                        query.setParameter(parameter, (Boolean) value);
                    }
                    else {
                    	if(value != null && !value.toString().isEmpty() && value.toString().contains(",")) {
                    		query.setParameter(parameter, Arrays.asList(value.toString().split(",")));
                    	}else {
                    		query.setParameter(parameter, (value == null ? "" : value.toString()));
                    	}
                    }
                }
            }
    	}
		List<Tuple> mappings = query.getResultList();
        if(mappings != null && mappings.size() > 0) {
        	result.success = true;
        	result.dto = new ArrayList<>();
            for(Tuple mapping : mappings) {
            	if(!Utils.isNullOrEmpty(mapping.get("ID").toString()) && !Utils.isNullOrEmpty(mapping.get("Text").toString())) {
            		LookupItemDTO itemInfo = new LookupItemDTO();
            		itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
            		itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
            		result.dto.add(itemInfo);
            	}
            }
        }
        return result;
    }
	
	public static String getMonthName(Integer monthId) {
        String month = "";
     	DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (monthId >= 1 && monthId <= 12 ) {
            month = months[monthId-1];
        }
        return month;   	
   }
	
   public static String[] GetStringArray(ArrayList<String> arr) { 
       String str[] = new String[arr.size()]; 
       for (int j = 0; j < arr.size(); j++) {  
           str[j] =   arr.get(j); 
       } 
       return str; 
   }

    /*public static Mono<ApiResult> uploadFiles(Flux<FilePart> data, String filePath, String[] fileTypeAccept) {
        Tika tika = new Tika();
        ApiResult result = new ApiResult();
        return data.takeWhile(item -> {
            File file = new File(filePath+item.filename());
            try {
                item.transferTo(file);
                String detectFileType = tika.detect(file);
                //System.out.println(detectFileType);
                result.success = (Arrays.stream(fileTypeAccept).anyMatch(detectFileType::contains)) ? true : false;//----Improves performance if size of the array is less.
                //result.success = (Arrays.stream(fileTypeAccept).parallel().anyMatch(detectFileType::contains)) ? true : false;
                if(!result.success) {
                    result.failureMessage = "notSupported";
                }
            }
            catch (IOException e) {
                result.success = false;
                result.failureMessage= e.getMessage();
            }
            finally {
                if(!result.success) {
                    data.map(item1 -> {
                        File file1 = new File(filePath + item1.filename());
                        if(file1.exists()) {
                            file1.delete();
                        }
                        return Mono.just(result);
                    }).subscribe();
                    return false;
                }
            }
            return true;
        }).then(Mono.just(result));
    }*/
    public static Mono<ApiResult> uploadFiles(Flux<FilePart> data, String filePath, String[] fileTypeAccept) {
        ApiResult result = new ApiResult();
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        atomicBoolean.set(true);
        final Path basePath = Paths.get(filePath);
        return data
                .flatMap(fp ->fp.transferTo(basePath.resolve(fp.filename())))
                .doOnComplete(()-> {
                    Tika tika = new Tika();
                    data.handle((fp, synchronousSink) -> {
                        String detectType =  tika.detect(filePath+fp.filename());
                        if(!(Arrays.stream(fileTypeAccept).anyMatch(detectType::contains))) {
                            atomicBoolean.set(false);
                            deleteFiles(data,filePath).subscribe();
                            synchronousSink.complete();
                        }
                    }).subscribe();
                    result.setSuccess(atomicBoolean.get());
                    if(result.success==false) {
                        result.setFailureMessage("File is not supported");
                    }
                }).then(Mono.just(result));
    }

    private static Mono<Void> deleteFiles(Flux<FilePart> data,String filePath) {
        return data.map(fp-> {
            File file = new File(filePath+fp.filename());
            if(file.exists()) {
                file.delete();
            }
            return Mono.empty();
        }).then();
    }

    public static Mono<ApiResult> generateFiles(Map<String,byte[]> data, String filePath) {
        ApiResult result = new ApiResult();
        try {
            for(Entry<String,byte[]> entry : data.entrySet()) {
                File file;
                FileOutputStream fop = null;
                try {
                    file = new File(filePath + entry.getKey());
                    fop = new FileOutputStream(file);
                    /*if (!file.exists()) {
                        file.createNewFile();
                    }*/
                    fop.write(entry.getValue());
                    fop.flush();
                    fop.close();
                    result.success = true;
                }
                catch (IOException e) {
                    result.success = false;
                    result.failureMessage = e.getMessage();
                    break;
                }
                finally {
                    try {
                        if(fop != null) {
                            fop.close();
                        }
                    }
                    catch (IOException e) {
                        result.success = false;
                        result.failureMessage = e.getMessage();
                        break;
                    }
                }
            }
        }
        finally {
            if(!result.success) {
                data.forEach((k, v) -> {
                    File file1 = new File(filePath + k);
                    if(file1.exists()) {
                        file1.delete();
                    }
                });
            }
        }
        return Mono.just(result);
    }

    public static Object getUniqueResult(List<Object> list) throws Exception {
        if(!Utils.isNullOrEmpty(list)) {
            if (list.size() == 1) return list.get(0);
            throw new NonUniqueResultException();
        }
        else {
            return null;
        }
    }
    
    public static long getDaysDifference(String startDate,String endDate) throws Exception{
        LocalDate stDate = convertStringDateTimeToLocalDate(startDate);
        LocalDate edDate = convertStringDateTimeToLocalDate(endDate);
        long diff = Duration.between(edDate.atStartOfDay(),stDate.atStartOfDay()).toDays();
		return diff;
	}

    public static byte[] getMD5CheckSumSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
	    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
	    byte[] salt = new byte[16];
	    sr.nextBytes(salt);
	    return salt;
	 }
	 
	public static String createMD5CheckSum(String fileName, byte[] salt) throws NoSuchAlgorithmException {
		 String md5HashFileName = null;
	     try {
	         MessageDigest md = MessageDigest.getInstance("MD5");
	         md.update(salt);
	         byte[] bytes = md.digest(fileName.getBytes());
	         StringBuilder sb = new StringBuilder();
	         for(int i=0; i< bytes.length ;i++) {
	        	 sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	         }
	         md5HashFileName = sb.toString();
	     } 
	     catch (NoSuchAlgorithmException e) {
	    	 e.printStackTrace();
	     }
	     return md5HashFileName;
	}
	
	public static String removeFileExtension(String fileName) { 
		if(null != fileName && fileName.contains(".")) {
			return fileName.substring(0, fileName.lastIndexOf("."));
		}
		return null;
	}
	
	public static String getFileExtension(String fileName) { 
		if(null != fileName && fileName.contains(".")) {
			return fileName.substring(fileName.lastIndexOf("."),fileName.length());
		}
		return null;
	}
	
	public static Set<Integer> GetCampusDepartmentMappingIds(String[] dataArray, Set<Integer> ids) {
		for (String data : dataArray) {
			if (data!=null && !data.isEmpty()) {
				StringBuffer id = new StringBuffer();										
				for (int i=0;i<data.length();i++) {
					char ch = data.charAt(i);
					if(ch=='-') {
						 break;
					}													
					id.append(ch);
				}
				if (!Utils.isNullOrEmpty(id)) {
					ids.add(Integer.valueOf(id.toString().trim()));
				}
			}
		}
		return ids;
	}

    public static boolean isValidEmail(String email) {
        String mailPattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        return Pattern.compile(mailPattern).matcher(email).matches();
    }
             
    public static int getTotalAdditionalPrivilageAllowed() {
    	return 5;
    }

    public static <T> Mono<T> monoFromObject(@Nullable T data) {
        EntityManagerInstance.closeEntityManager();
        return Mono.justOrEmpty(data);
    }

    public static ApiResult responseResult(boolean val) {
        ApiResult result = new ApiResult();
        result.success = val;
        return  result;
    }
    
    public static String htmlToText(String html) {
    	return Jsoup.parse(html).text();
    }
        
    public static LocalDate convertStringDateToLocalDate(String date) {
    	DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
    			.appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("dd/MMM/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("d/M/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("dd/M/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("MM/d/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    			.appendOptional(DateTimeFormatter.ofPattern("DD/MM/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("MM/DD/YYYY"))
    			.appendOptional(DateTimeFormatter.ofPattern("M/dd/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("M/dd/yyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyy"))
    			.appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)
    			.toFormatter();
    	return LocalDate.parse(date, dateTimeFormatter);
    }

    public static String convertLocalDateToStringDate(LocalDate localDate) {
    	return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); 
    }
    
    public static LocalTime convertStringTimeToLocalTime(String time) {
    	//return LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
    //	return LocalTime.parse(time, DateTimeFormatter.ofPattern("hh:mm a",Locale.ENGLISH));
    	DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
    			.appendOptional(DateTimeFormatter.ofPattern("hh:mm a"))
    			.appendOptional(DateTimeFormatter.ISO_LOCAL_TIME)
    			.toFormatter(Locale.ENGLISH);
    	return LocalTime.parse(time, dateTimeFormatter);	
    }

    public static String convertLocalTimeToStringTime(LocalTime localTime) {
    	return localTime.format(DateTimeFormatter.ofPattern("hh:mm a")).toUpperCase();
    }
    
    public static String convertLocalTimeToStringTime1(LocalTime localTime) {
    	return localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static LocalDateTime convertStringDateTimeToLocalDateTime(String date) {
    	DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
    			.appendOptional(DateTimeFormatter.ISO_DATE_TIME)
    			.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"))
    			.appendOptional(DateTimeFormatter.ofPattern("dd/MMM/yyyy hh:mm a"))
    			.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd K:mm"))
    			.toFormatter();
    	LocalDateTime localDateTime;
    	if(date.contains("Z")) {
    		localDateTime = ZonedDateTime.parse(date, dateTimeFormatter).withZoneSameInstant(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
    	}else {
    		localDateTime = LocalDateTime.parse(date,dateTimeFormatter);
    	}
    	return localDateTime;
    }

    public static String convertLocalDateTimeToStringDateTime(LocalDateTime localDateTime) {
    	return localDateTime.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy hh:mm a")).toUpperCase();  
    }	

    public static LocalDate  convertStringDateTimeToLocalDate(String dateTime) {  	
    	return (convertStringDateTimeToLocalDateTime(dateTime)).toLocalDate();
    }

    //  public static LocalTime  convertStringDateTimeToLocalTime(String dateTime) {  	
    //	return (convertStringDateTimeToLocalDateTime(dateTime)).toLocalTime();
    //}

    public static String convertLocalDateTimeToStringDate(LocalDateTime localDateTime) {
    	return localDateTime.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy"));
    }

    public static String convertLocalDateTimeToStringTime(LocalDateTime localDateTime) {
    	return localDateTime.format(DateTimeFormatter.ofPattern("hh:mm a")).toUpperCase();
    }
    
	  public static String convertLocalDateToStringDate1(LocalDate localDate) {
	    	return localDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
	    }
	  
	  public static String convertLocalDateToStringDate2(LocalDate localDate) {
	    	return localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	    }
	  
	  public static String convertLocalDateToStringDate3(LocalDate localDate) {
	    	return localDate.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy")); 
	    }
	  
	  public static String convertLocalDateToStringDate4(LocalDate localDate) {
	    	return localDate.format(DateTimeFormatter.ofPattern("dd MMMM")); 
	    }
	  
	  public static String convertLocalDateToStringDate5(LocalDate localDate) {
	    	return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")); 
	    }
	  
	public static int calculateSundaysForDateRange(LocalDate startDate, LocalDate endDate) {
		int noOfSunday = Stream.iterate(startDate, d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate) + 1).filter(localDate3 -> {
                    DayOfWeek d = localDate3.getDayOfWeek();
                    return  d == DayOfWeek.SUNDAY;
                }).collect(Collectors.toList()).size();
		System.out.println("sunday count1" +noOfSunday);
		return noOfSunday;
	}

	public static boolean checkISSunday(LocalDate localDate) {
		DayOfWeek d = localDate.getDayOfWeek();
		if (d == DayOfWeek.SUNDAY) {
			return true;
		} else {
			return false;
		}
	}
	
	//"Mon Nov 09 2015 00:00:00 GMT+0530 (India Standard Time)" to date
	public static LocalDate convertStringDateTimeToLocalDate1(String date) {
		DateTimeFormatter dtfInput = new DateTimeFormatterBuilder()
				.parseCaseInsensitive()
				.appendPattern("EEE MMM dd yyyy HH:mm:ss 'GMT' SSSS")
				.appendLiteral(" ")
				.appendZoneId()
				.appendPattern("X")
				.appendLiteral(" ")
				.appendLiteral("(")
				.appendGenericZoneText(TextStyle.FULL)
				.appendLiteral(')')
				.toFormatter(Locale.ENGLISH);
		ZonedDateTime zdt = ZonedDateTime.parse(date, dtfInput);
		OffsetDateTime odt = zdt.toOffsetDateTime();
		LocalDate localDate = odt.toLocalDate();
		return localDate;
	}
	
	public static LocalDateTime convertStringDateTimeToLocalDateTime1(String date) {
		DateTimeFormatter dtfInput = new DateTimeFormatterBuilder()
				.parseCaseInsensitive()
				.appendPattern("E MMM d uuuu H:m:s")
				.appendLiteral(" ")
				.appendZoneId()
				.appendPattern("X")
				.appendLiteral(" ")
				.appendLiteral("(")
				.appendGenericZoneText(TextStyle.FULL)
				.appendLiteral(')')
				.toFormatter(Locale.ENGLISH);
		ZonedDateTime zdt = ZonedDateTime.parse(date, dtfInput);
		OffsetDateTime odt = zdt.toOffsetDateTime();
        LocalDateTime localDateTime = odt.toLocalDateTime();
		return localDateTime;
	}
	public static String convertLocalDateTimeToStringDate1(LocalDateTime localDateTime) {
		return localDateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-DD"));
	}	
	
	public static LocalDate convertStringDateToLocalDate1(String date) {
	    	DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
	    			.appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
	    			.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
	    			.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
	    			.appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)
	    			.toFormatter();
	    	return LocalDate.parse(date, dateTimeFormatter);
	 }
	
	 public static String convertLocalDateToStringDate6(LocalDate localDate) {
	    return localDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")); 
	  }

    public static LocalDateTime convertStringLocalDateTimeToLocalDateTime(String localDateString) {
        return LocalDateTime.parse(localDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getBlobObjectToString(Object object) {
        String msgBody= null;
        Object content = object;
        if (content instanceof String) {
            msgBody = (String) content;
        } else if (content instanceof Blob) {
            Blob blob = (Blob) content;
            byte[] bytes;
            try {
                bytes = blob.getBytes(1, (int) blob.length());
                msgBody = new String(bytes);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return msgBody;
    }

  }

