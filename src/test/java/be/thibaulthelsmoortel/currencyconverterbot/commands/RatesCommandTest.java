/*
 * Copyright (c) 2019 Thibault Helsmoortel.
 *
 * This file is part of Currency Converter Bot.
 *
 * Currency Converter Bot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Currency Converter Bot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Currency Converter Bot.  If not, see <https://www.gnu.org/licenses/>.
 */

package be.thibaulthelsmoortel.currencyconverterbot.commands;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.thibaulthelsmoortel.currencyconverterbot.api.model.Currency;
import be.thibaulthelsmoortel.currencyconverterbot.api.model.Rate;
import be.thibaulthelsmoortel.currencyconverterbot.api.parsers.RatesParser;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.events.Event;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * @author Thibault Helsmoortel
 */
class RatesCommandTest extends CommandBaseTest {

    private RatesCommand ratesCommand;

    @MockBean
    private RatesParser ratesParser;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        this.ratesCommand = new RatesCommand(ratesParser);
        List<Rate> rates = new ArrayList<>();
        addRate(rates, "USD", "0.7532");
        addRate(rates, "CAD", "1.0248");
        when(ratesParser.parse()).thenReturn(rates);
        ratesCommand.setEvent(messageReceivedEvent);
    }

    private void addRate(List<Rate> rates, String isoCode, String rawRate) {
        Rate rate = new Rate();
        Currency currency = new Currency();
        currency.setIsoCode(isoCode);
        rate.setCurrency(currency);
        rate.setValue(new BigDecimal(rawRate));
        rates.add(rate);
    }

    @DisplayName("Should send rates message.")
    @Test
    void shouldSendRatesMessage() {
        String message = (String) ratesCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertTrue(message.contains("USD"), "Message should contain USD.");
        Assertions.assertTrue(message.contains("CAD"), "Message should contain CAD.");
        verifyOneMessageSent(message);
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        RatesCommand ratesCommand = new RatesCommand(ratesParser);

        verifyDoNotProcessEvent(ratesCommand, mock(Event.class));
    }
}
