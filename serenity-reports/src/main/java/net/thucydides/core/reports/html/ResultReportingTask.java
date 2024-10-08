package net.thucydides.core.reports.html;

import com.google.common.base.Objects;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ResultReportingTask extends BaseReportingTask implements ReportingTask {

    private static final String TEST_OUTCOME_TEMPLATE_PATH = "freemarker/outcomes-with-result.ftl";

    final FreemarkerContext freemarker;
    final EnvironmentVariables environmentVariables;
    final File outputDirectory;
    final TestOutcomes testOutcomes;
    final ReportNameProvider reportNameProvider;
    final TestTag tag;
    final String testResult;
    final String reportName;

    public ResultReportingTask(FreemarkerContext freemarker,
                               EnvironmentVariables environmentVariables,
                               File outputDirectory,
                               TestOutcomes testOutcomes,
                               ReportNameProvider reportNameProvider,
                               TestTag tag,
                               String testResult) {
        super(freemarker, environmentVariables, outputDirectory);
        this.freemarker = freemarker;
        this.environmentVariables = environmentVariables;
        this.outputDirectory = outputDirectory;
        this.testOutcomes = testOutcomes;
        this.reportNameProvider = reportNameProvider;
        this.tag = tag;
        this.testResult = testResult;
        this.reportName = reportNameProvider.withPrefix(tag).forTestResult(testResult);
    }

    @Override
    public String reportName() {
        return reportName;
    }

    @Override
    public void generateReports() throws IOException {
        Map<String, Object> context = freemarker.getBuildContext(testOutcomes, reportNameProvider, true);
        context.put("report", ReportProperties.forTestResultsReport());
        context.put("currentTagType", tag.getType());
        context.put("currentTag", tag);
        context.put("reportNameInContext", reportNameProvider.inContext(tag.getCompleteName()));

        String csvReport = reportNameProvider.forCSVFiles().forTestResult(testResult);
        context.put("csvReport", csvReport);
        generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, reportName);
        generateCSVReportFor(testOutcomes, csvReport);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultReportingTask that = (ResultReportingTask) o;
        return Objects.equal(reportName, that.reportName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(reportName);
    }

    @Override
    public String toString() {
        String sb = "ResultReportingTask{" + "reportName='" + reportName + '\'' +
                '}';
        return sb;
    }
}
