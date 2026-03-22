package drinkshop.service.validator;

import drinkshop.domain.Product;

public class ProductValidator implements Validator<Product> {

    @Override
    public void validate(Product product) {

        String errors = "";

        if (product.getId() <= 0)
            errors += "ID invalid!\n";

        if (product.getNume() == null || product.getNume().trim().isEmpty()) {
            errors += "Numele nu trebuie sa fie vid!\n";
        } else if (product.getNume().length() > 50) {
            errors += "Numele trebuie sa aiba <=50 caractere!\n";
        }

        if (product.getPret() < 0.0) {
            errors += "Pretul trebuie sa fie >= 0.0!\n";
        } else if (product.getPret() > 10000.0) {
            errors += "Pretul trebuie sa fie <= 10000.0!\n";
        }

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
