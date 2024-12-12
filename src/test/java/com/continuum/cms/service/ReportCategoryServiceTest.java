package com.continuum.cms.service;


import com.continuum.cms.dao.ReportDao;
import com.continuum.cms.entity.Report;
import com.continuum.cms.mapper.ReportMapper;
import com.continuum.cms.model.request.ReportFilter;
import com.continuum.cms.model.response.ReportResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.util.AppConstants;
import com.continuum.cms.util.ReportsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ReportCategoryServiceTest {

    @Mock
    private ReportDao reportDao;
    @Mock
    private ReportMapper reportMapper;


    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testReportListSuccess() {
        ReportFilter reportFilter = new ReportFilter();
        reportFilter.setCategories(List.of("vendor"));
        String search = "testSearch";
        Pageable pageable = mock(Pageable.class);
        Page<Report> reportsPage = mock(Page.class);
        List<Report> reportList = new ArrayList<>();
        reportList.add(ReportsBuilder.buildReports(UUID.randomUUID(), UUID.randomUUID(), "vendor", "filter", "Available"));
        reportList.add(ReportsBuilder.buildReports(UUID.randomUUID(), UUID.randomUUID(), "carbon credit", "filter", "Available"));

        when(reportDao.getReportList(reportFilter, search, pageable)).thenReturn(reportsPage);
        when(reportsPage.hasContent()).thenReturn(true);
        when(reportsPage.getContent()).thenReturn(reportList);
        when(reportsPage.stream()).thenReturn(reportList.stream());

        List<ReportResponse> reportResponses = new ArrayList<>();
        ReportResponse reportResponse1 = new ReportResponse();
        ReportResponse reportResponse2 = new ReportResponse();
        reportResponses.add(reportResponse1);
        reportResponses.add(reportResponse2);
        when(reportMapper.mapToReportList(reportList)).thenReturn(reportResponses);

        ResponseDto<ReportResponse> responseDto = reportService.reportList(reportFilter, search, pageable);

        assertEquals(AppConstants.RESPONSE_SUCCESS, responseDto.getStatus());
        assertEquals(reportResponses.size(), responseDto.getData().size());
        assertEquals(reportResponses.size(), responseDto.getCount());
    }

    @Test
    public void testReportListNullSearchString() {
        ReportFilter reportFilter = new ReportFilter();
        reportFilter.setCategories(List.of("vendor"));
        String search = null;
        Pageable pageable = mock(Pageable.class);
        Page<Report> reportsPage = mock(Page.class);
        List<Report> reportList = new ArrayList<>();
        Report report1 = new Report();
        Report report2 = new Report();
        reportList.add(report1);
        reportList.add(report2);

        when(reportDao.getReportList(reportFilter, search, pageable)).thenReturn(reportsPage);
        when(reportsPage.hasContent()).thenReturn(true);
        when(reportsPage.getContent()).thenReturn(reportList);
        when(reportsPage.stream()).thenReturn(reportList.stream());

        List<ReportResponse> reportRespons = new ArrayList<>();
        ReportResponse re = new ReportResponse();
        ReportResponse re1 = new ReportResponse();
        reportRespons.add(re);
        reportRespons.add(re1);
        when(reportMapper.mapToReportList(reportList)).thenReturn(reportRespons);

        ResponseDto<ReportResponse> responseDto = reportService.reportList(reportFilter, search, pageable);

        assertEquals(AppConstants.RESPONSE_SUCCESS, responseDto.getStatus());
        assertEquals(reportRespons.size(), responseDto.getData().size());
        assertEquals(reportRespons.size(), responseDto.getCount());
    }

    @Test
    public void testReportListEmptyList() {
        ReportFilter reportFilter = new ReportFilter();
        reportFilter.setCategories(List.of("carbon_credit"));
        String search = "testSearch";
        Pageable pageable = mock(Pageable.class);
        Page<Report> reportsPage = mock(Page.class);

        when(reportDao.getReportList(reportFilter, search, pageable)).thenReturn(reportsPage);
        when(reportsPage.hasContent()).thenReturn(false);

        ResponseDto<ReportResponse> responseDto = reportService.reportList(reportFilter, search, pageable);

        assertEquals(AppConstants.RESPONSE_SUCCESS, responseDto.getStatus());
        assertEquals(0, responseDto.getData().size());
        assertEquals(0, responseDto.getCount());
        assertEquals(0, responseDto.getTotal());
    }
}
