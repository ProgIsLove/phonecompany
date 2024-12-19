package TelephoneBillCalculator;

import com.phonecompany.billing.telephonebillcalculator.TelephoneBillCalculator;
import com.phonecompany.billing.telephonebillcalculator.TelephoneBillCalculatorImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TelephoneBillCalculatorImplTest {

    private final TelephoneBillCalculator calculator = new TelephoneBillCalculatorImpl();

    @Test
    void testSingleCallWithinLowRateTime() {
        String phoneLog = """
                420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57
                420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00""";
        BigDecimal totalCost = calculator.calculate(phoneLog);
        assertEquals(new BigDecimal("1.5"), totalCost);
    }


    @Test
    void testMultipleCallsWithPromotion() {
        String phoneLog = """
                420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57
                420774577453,14-01-2020 18:10:15,14-01-2020 18:12:57
                420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00""";
        BigDecimal totalCost = calculator.calculate(phoneLog);
        assertEquals(new BigDecimal("3.0"), totalCost);
    }

    @Test
    void testCallExceedingFiveMinutes() {
        String phoneLog = """
                420774577454,13-01-2020 18:10:15,13-01-2020 18:12:57
                420774577454,13-01-2020 19:10:15,13-01-2020 19:12:57
                420776562354,18-01-2020 08:59:20,18-01-2020 09:10:00
                420776562354,18-01-2020 09:15:20,18-01-2020 09:25:20
                420776562353,18-01-2020 08:59:20,18-01-2020 09:10:20""";
        BigDecimal totalCost = calculator.calculate(phoneLog);
        assertEquals(new BigDecimal("9.2"), totalCost);
    }

    @Test
    void testMultipleCallsWithDifferentNumbers() {
        String phoneLog = """
                420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57
                420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00
                420774577454,19-01-2020 20:00:00,19-01-2020 20:10:00""";
        BigDecimal totalCost = calculator.calculate(phoneLog);
        assertEquals(new BigDecimal("5.0"), totalCost);
    }

    @Test
    void testNoCalls() {
        String phoneLog = "";
        BigDecimal totalCost = calculator.calculate(phoneLog);
        assertEquals(new BigDecimal("0"), totalCost);
    }
}
