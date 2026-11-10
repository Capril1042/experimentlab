package com.experimentlab.variant;

import com.experimentlab.demo.DemoSession;
import com.experimentlab.demo.DemoSessionResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/experiments/{experimentId}/variants")
public class VariantController {

    private final VariantService variantService;
    private final DemoSessionResolver demoSessionResolver;

    public VariantController(
            VariantService variantService,
            DemoSessionResolver demoSessionResolver
    ) {
        this.variantService = variantService;
        this.demoSessionResolver = demoSessionResolver;
    }

    @GetMapping
    public List<VariantResponse> getVariants(
            @PathVariable UUID experimentId,
            HttpServletRequest request
    ) {
        DemoSession session = resolveSession(request);

        return variantService.getVariants(
                experimentId,
                session.getId()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VariantResponse createVariant(
            @PathVariable UUID experimentId,
            @RequestBody CreateVariantRequest body,
            HttpServletRequest request
    ) {
        DemoSession session = resolveSession(request);

        return variantService.createVariant(
                experimentId,
                session.getId(),
                body
        );
    }

    @PutMapping("/{variantId}")
    public VariantResponse updateVariant(
            @PathVariable UUID experimentId,
            @PathVariable UUID variantId,
            @RequestBody UpdateVariantRequest body,
            HttpServletRequest request
    ) {
        DemoSession session = resolveSession(request);

        return variantService.updateVariant(
                experimentId,
                variantId,
                session.getId(),
                body
        );
    }

    @DeleteMapping("/{variantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVariant(
            @PathVariable UUID experimentId,
            @PathVariable UUID variantId,
            HttpServletRequest request
    ) {
        DemoSession session = resolveSession(request);

        variantService.deleteVariant(
                experimentId,
                variantId,
                session.getId()
        );
    }

    private DemoSession resolveSession(HttpServletRequest request) {
        return demoSessionResolver.resolve(request)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Valid demo session required"
                ));
    }
}

