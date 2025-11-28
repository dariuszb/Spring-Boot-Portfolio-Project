package org.example.repository.book.specification;

import java.util.Arrays;
import org.example.model.Book;
import org.example.repository.user.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsbnSpecification implements SpecificationProvider<Book> {

    @Override
    public String getKey() {
        return "isbn";
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> root.get("isbn").in(Arrays.stream(params).toArray());
    }
}

/*public List<BankAccount> findAllWhereTheAmountBetween(BigDecimal from, BigDecimal to) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<BankAccount> criteriaQuery =
            criteriaBuilder.createQuery(BankAccount.class);
            Root<BankAccount> bankAccountRoot = criteriaQuery
            .from(BankAccount.class); // SQL => SELECT * FROM accounts;
            Predicate amountGt = criteriaBuilder.gt(bankAccountRoot.get("amount"), from);
            Predicate amountLt = criteriaBuilder.lt(bankAccountRoot.get("amount"), to);
            Predicate and = criteriaBuilder.and(amountGt, amountLt);
            criteriaQuery.where(and);

            return session.createQuery(criteriaQuery).getResultList();
        }
    }*/
