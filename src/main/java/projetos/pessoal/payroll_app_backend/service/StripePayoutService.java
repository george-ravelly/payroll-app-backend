package projetos.pessoal.payroll_app_backend.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.Transfer;
import com.stripe.net.RequestOptions;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.TransferCreateParams;

import projetos.pessoal.payroll_app_backend.model.Employee;
import projetos.pessoal.payroll_app_backend.model.Payroll;

@Service
public class StripePayoutService {

    // 1. Criar a conta conectada na Stripe e gerar o link para o funcionário preencher
    public String iniciarOnboardingFuncionario(Employee employee) throws StripeException {
        // Criar a conta Express
        AccountCreateParams params = AccountCreateParams.builder()
            .setType(AccountCreateParams.Type.EXPRESS)
            .setEmail(employee.getEmail())
            .setCountry("BR") // Se for no Brasil
            .setCapabilities(
                AccountCreateParams.Capabilities.builder()
                    .setTransfers(AccountCreateParams.Capabilities.Transfers.builder().setRequested(true).build())
                    .build()
            )
            .build();

        Account account = Account.create(params);
        
        // Salvar o account.getId() no documento do seu Employee no MongoDB (Crucial!)
        // employee.setStripeAccountId(account.getId());
        // employeeRepository.save(employee);

        // Criar o link de redirecionamento onde o funcionário vai digitar a conta bancária
        AccountLinkCreateParams linkParams = AccountLinkCreateParams.builder()
            .setAccount(account.getId())
            .setRefreshUrl("https://seuapp.com/recarregar-onboarding")
            .setReturnUrl("https://seuapp.com/sucesso-onboarding")
            .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
            .build();

        AccountLink accountLink = AccountLink.create(linkParams);
        
        return accountLink.getUrl(); // Retorne essa URL para o seu frontend abrir
    }

    public String realizarPagamentoSalarial(Payroll payroll, String stripeAccountId) throws StripeException {
        // A Stripe calcula valores em centavos (Ex: R$ 100,00 vira 10000)
        long valorEmCentavos = payroll.getNetSalary().multiply(new BigDecimal("100")).longValue();

        TransferCreateParams params = TransferCreateParams.builder()
            .setAmount(valorEmCentavos)
            .setCurrency("brl")
            .setDestination(stripeAccountId) // O ID da conta do funcionário criado na Etapa 1
            .setDescription("Folha de Pagamento - Ref: " + payroll.getReferencePeriod())
            .build();

        // Passando uma chave de Idempotência baseada no ID da folha do MongoDB
        RequestOptions requestOptions = RequestOptions.builder()
            .setIdempotencyKey("pay_" + payroll.getId())
            .build();

        Transfer transfer = Transfer.create(params, requestOptions);

        return transfer.getId(); // ID do comprovante da Stripe
    }
}