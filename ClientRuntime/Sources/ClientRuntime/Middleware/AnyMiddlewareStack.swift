// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

/// type erase the Middleware Stack protocol
public struct AnyMiddlewareStack<MInput, MOutput, Context: MiddlewareContext>: MiddlewareStack {
    
    public var orderedMiddleware: OrderedGroup<MInput, MOutput, Context>
    
    private let _handle: (Context, MInput, AnyHandler<MInput, MOutput, Context>) -> Result<MOutput, Error>

    public var id: String

    public init<M: MiddlewareStack>(_ realMiddleware: M)
    where M.MInput == MInput, M.MOutput == MOutput, M.Context == Context {
        if let alreadyErased = realMiddleware as? AnyMiddlewareStack<MInput, MOutput, Context> {
            self = alreadyErased
            return
        }

        self.id = realMiddleware.id
        self._handle = realMiddleware.handle
        self.orderedMiddleware = realMiddleware.orderedMiddleware
    }

    public func handle<H: Handler>(context: Context, input: MInput, next: H) -> Result<MOutput, Error>
    where H.Input == MInput, H.Output == MOutput, H.Context == Context {
        return _handle(context, input, next.eraseToAnyHandler())
    }
}