package stock_trading.dto;

import java.util.Map;

public record ValidationError(Map<String, String> errors) {
}
