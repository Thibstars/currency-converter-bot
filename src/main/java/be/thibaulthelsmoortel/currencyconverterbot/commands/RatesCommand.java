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

import static org.javamoney.moneta.convert.ecb.ECBCurrentRateProvider.BASE_CURRENCY;

import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.MonetaryConversions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * @author Thibault Helsmoortel
 */
@Command(name = "rates", description = "Provides current currency rates.")
@Component
public class RatesCommand extends BotCommand {

    private static final String HEADER = "Currency rates";

    // TODO: 05/12/2019 Add base currency option (default to EUR)

    // TODO: 05/12/2019 add exchange rate provider option (array of possible values: IDENT,ECB,IMF,ECB-HIST,ECB-HIST90)

    @Override
    public Object call() {
        MessageEmbed embed = null;

        if (getEvent() instanceof MessageReceivedEvent) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(HEADER);

            ExchangeRateProvider rateProvider = MonetaryConversions.getExchangeRateProvider();

            Collection<CurrencyUnit> currencies = Monetary.getCurrencies();

            List<ExchangeRate> exchangeRates = currencies.stream()
                .filter(currency -> rateProvider.isAvailable(BASE_CURRENCY.getCurrencyCode(), currency.getCurrencyCode()))
                .map(currency -> {
                    try {
                        return rateProvider.getExchangeRate(BASE_CURRENCY.getCurrencyCode(), currency.getCurrencyCode());
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ExchangeRate::getFactor))
                .collect(Collectors.toList());

            exchangeRates.forEach(
                exchangeRate -> embedBuilder.addField(exchangeRate.getCurrency().getCurrencyCode(), exchangeRate.getFactor().toString(), true));

            embed = embedBuilder.build();
            ((MessageReceivedEvent) getEvent()).getChannel().sendMessage(embed).queue();
        }

        return embed;
    }
}
