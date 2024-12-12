package com.continuum.cms.validator;

import com.continuum.cms.exceptions.ValidationException;
import com.continuum.cms.model.request.ReportFilter;
import com.continuum.cms.util.ErrorConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import static com.continuum.cms.util.enums.ReportCategory.CARBON_CREDIT;
import static com.continuum.cms.util.enums.ReportCategory.VENDOR;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReportValidation {
    public static void reportRequestValidation(ReportFilter reportFilter) {
        List<String> categories = reportFilter.getCategories();
        if (CollectionUtils.isEmpty(categories) ||
                (!VENDOR.getName().equalsIgnoreCase(categories.get(0)) && !CARBON_CREDIT.getName().equalsIgnoreCase(categories.get(0)))) {
            throw new ValidationException(ErrorConstants.MANDATORY_ERROR_CODE,
                    MessageFormat.format(ErrorConstants.MANDATORY_ERROR_MESSAGE, "Category"));
        }
        dateRangeFilter(reportFilter);
    }

    public static void dateRangeFilter(ReportFilter reportFilter){
        Date startDate = reportFilter.getStartDate();
        Date endDate = reportFilter.getEndDate();
        if (ObjectUtils.isNotEmpty(startDate) && ObjectUtils.isNotEmpty(endDate)) {
            if (!endDate.equals(startDate) && endDate.before(startDate)) {
                throw new ValidationException(ErrorConstants.INVALID_DATE_RANGE_CODE,
                        ErrorConstants.INVALID_DATE_RANGE_MESSAGE);
            }
        }
    }
}
