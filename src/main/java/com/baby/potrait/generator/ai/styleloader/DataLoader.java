// package com.baby.potrait.generator.ai.styleloader;
//
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// import com.baby.potrait.generator.ai.entity.Style;
// import com.baby.potrait.generator.ai.repository.StyleRepository;
//
// @Configuration
// public class DataLoader {
//
//     @Bean
//     CommandLineRunner initStyles(StyleRepository styleRepository) {
//         return args -> {
//             if (styleRepository.count() == 0) {
//                 styleRepository.save(new Style(null, "Cartoon", null,
//                         "A cute cartoon portrait of a baby",
//                         "https://res.cloudinary.com/durjsnekq/image/upload/v1756538772/cartoon_ghw6qj.jpg"));
//
//                 styleRepository.save(new Style(null, "Bike", null,
//                         "A cute baby riding a bike",
//                         "https://res.cloudinary.com/durjsnekq/image/upload/v1756538771/bk-baby_rxjj0i.jpg"));
//
//                 styleRepository.save(new Style(null, "Baby Thor", null,
//                         "A realistic portrait of a baby in marvel superhero thor appearance",
//                         "https://res.cloudinary.com/durjsnekq/image/upload/v1756538760/baby-thor_rmednc.webp"));
//             }
//         };
//     }
// }
